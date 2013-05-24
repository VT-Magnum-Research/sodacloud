#
#
# Setup: create a file called "config.soda" in the working directory and insert:
#
# aws_access_key:<aws access key>
# aws_secret_key:<aws secret key>
# ssh_key:<path to your key.pem>
#
#
# Sample Usage:
#    fab deploy:ami=ami-05355a6c,keyname=soda,userdata=test123,type=t1.micro,securitygroup=docker-ec2 ensure_up get_user_data
# -- launches an EC2 instance of the specified type, waits for ssh to come up on the instance
#    and then grabs and prints the user data for each node


from __future__ import with_statement
import time
import boto.ec2
from boto.ec2.connection import EC2Connection
from fabric.api import *
from fabric.contrib.console import confirm

MAXIMUM_NUMBER_OF_ATTEMPTS = 12

config = {}
with open("config.soda") as f:
    for line in f:
       (key, val) = line.strip().split(":")
       config[key] = val


AWS_ACCESS_KEY = config['aws_access_key']
AWS_SECRET_KEY = config['aws_secret_key']
env.key_filename = config['ssh_key']

conn = EC2Connection(AWS_ACCESS_KEY, AWS_SECRET_KEY)




def install_docker():
	put("./scripts/install_docker.sh","/install_docker.sh",mode=0755,use_sudo=True)
	run("/install_docker.sh")  
	
def install_s3cmd():
	run("sudo apt-get install s3cmd") 
	put("scripts/s3cfg","/home/ubuntu/.s3cfg",mode=0700)
	run("sed -i -e's/__ak__/%s/' /home/ubuntu/.s3cfg" % AWS_ACCESS_KEY)
	run("sed -i -e's/__pk__/%s/' /home/ubuntu/.s3cfg" % AWS_SECRET_KEY)
	run("sed -i -e's/__pf__/%s/' /home/ubuntu/.s3cfg" % AWS_SECRET_KEY)
    
def ensure_running(instances):
	print("Waiting for %i to come up" % len(instances))

	for instance in instances:
		print("checking instance %s" % instance)
		while not instance.update() == 'running':
  			time.sleep(5)
  			
 	for instance in instances:
		print("Instance %s host:%s is up" % (instance, instance.public_dns_name))

def ensure_ssh_is_up():
	for attempt in range(MAXIMUM_NUMBER_OF_ATTEMPTS):
	    try:
	    	print("Checking ssh on running instances...")
	        run("uname -a")
	        print("SSH is up!")
	    except: # replace " as " with ", " for Python<2.6
	    	print("SSH is not up yet, retrying...")
	        time.sleep(10)
	    else: # we tried, and we had no failure, so
	        break
	else: # we never broke out of the for loop
   		raise RuntimeError("maximum number of unsuccessful attempts reached")

def ensure_up():
	print("Ensuring SSH is up on %s" % env.hosts);
	ensure_ssh_is_up();

def get_user_data():
	run("wget http://169.254.169.254/latest/user-data")
	run("cat user-data")

def create_docker_image_from_s3(imgid,bucket,key):
	install_s3cmd()
	run("sudo s3cmd --no-check-md5 --no-progress get s3://%s/%s /%s" % (bucket,key,imgid))
	create_docker_image_with_remote_file("/"+imgid,imgid)

def create_docker_image_from_file(imgid,img):
	#with settings(hosts=instances):
		put(img,"/" + imgid,mode=0755,use_sudo=True)
		create_docker_image_with_remote_file("/" + imgid, imgid)
		
def create_docker_image_with_remote_file(imgfile,imgid):		
	run("sudo cat %s | sudo docker import - %s" % (imgfile,imgid))

def run_in_docker(imgid,cmd,port):
	run("docker run -i -t -p %s %s %s" % (port,imgid,cmd))

def deploy_docker_instance(keyname,userdata="docker",type="t1.micro",securitygroup="docker-ec2"):
	deploy(ami="ami-3be88052",
			user="ubuntu",
			keyname=keyname,
			userdata=userdata,
			type=type,
			securitygroup=securitygroup)

def deploy(ami,keyname,user,userdata,type,securitygroup):
	
	res = conn.run_instances(
       	 	ami,
       	 	key_name=keyname,
        	user_data=userdata,
        	instance_type=type,
        	security_groups=[securitygroup])
	
	ensure_running(instances=res.instances)
	
	launched = [user + "@" + instance.public_dns_name for instance in res.instances]
	print("Launched: %s" % launched)
	
	env.hosts = launched;
	
