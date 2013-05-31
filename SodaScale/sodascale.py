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
import requests
import boto.ec2
import os.path
import csv
import uuid
from boto.ec2.connection import EC2Connection
from fabric.api import *
from fabric.contrib.project import *
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

def apt_install(pkg):
    run("sudo apt-get install -y %s" % pkg) 

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
    print("Waiting for %i instance(s) to come up" % len(instances))

    for instance in instances:
        print("Checking instance %s" % instance)
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
    return run("docker run -i -t -d -p %s %s %s" % (port,imgid,cmd))

def deploy_docker_instance(keyname,userdata="docker",type="t1.micro",securitygroup="docker-ec2"):
    deploy(ami="ami-3be88052",
            user="ubuntu",
            keyname=keyname,
            userdata=userdata,
            type=type,
            securitygroup=securitygroup)

def deploy(ami,keyname,user,userdata,type,securitygroup):
    
    print("Launching: %s on %s" % (ami,type))
    
    res = conn.run_instances(
                ami,
                key_name=keyname,
            user_data=userdata,
            instance_type=type,
            security_groups=[securitygroup])
    
    ensure_running(instances=res.instances)
    
    hosts = [user + "@" + instance.public_dns_name for instance in res.instances]
    launched = [{'host':instance.public_dns_name,
                 'host_string':user + "@" + instance.public_dns_name,
                 'id':instance.id} for instance in res.instances]
    print("Launched: %s on %s" % (launched,type))

    env.hosts = hosts
    return launched
    
def test_app_on_nodes(keyname,userdata="docker",type="t1.micro",securitygroup="docker-ec2"):
    deploy_docker_instance(keyname=soda) 
    ensure_up 
    get_user_data 
    #create_docker_image_from_s3:imgid=soda,bucket=sodascale,key=soda-server.pkg run_in_docker:imgid=soda,cmd="java -jar /soda/soda-server.jar",port=8081
    


output_prefix = ""
exp_time = time.strftime("%d-%m-%Y_%H-%M-%S", time.gmtime())
app_nodes = []
test_nodes = []
test_node_instances = []
app_node_instances = []

key_name = ""

def experiment(name="experiment"):
 
     def exp_decorator(target):
         target.experiment_name = name
         return target
     return exp_decorator

def app_node(ami,
             key_pair,
             type="t1.micro",
             instances=1,
             user="ubuntu",
             security_group="default",
             user_data="",
             ssh_key=key_name,
             requires=[],
             tags=[],
             terminate_when_done=True,
             stop_when_done=False):
 
     def decorator(target):
         node_config =  {
                           'config':target,
                           'ami':ami,
                           'type':type,
                           'instances':instances,
                           'tags':tags,
                           'user':user,
                           'security_group':security_group,
                           'key_pair':key_pair,
                           'user_data':user_data,
                           'ssh_key':ssh_key,
                           'terminate_when_done':terminate_when_done,
                           'stop_when_done':stop_when_done
                           }
         app_nodes.append(node_config)
         
         target.node_config = node_config
         target.instances = []
         return target
     return decorator
 
def test_node(ami,
             key_pair,
             type="t1.micro",
             instances=1,
             user="ubuntu",
             security_group="default",
             user_data="",
             ssh_key=key_name,
             requires=[],
             tags=[],
             terminate_when_done=True,
             stop_when_done=False):
 
 
     def test_decorator(target):
         node_config =  {
                           'name':target.__name__,
                           'config':target,
                           'ami':ami,
                           'type':type,
                           'instances':instances,
                           'tags':tags,
                           'user':user,
                           'security_group':security_group,
                           'key_pair':key_pair,
                           'user_data':user_data,
                           'ssh_key':ssh_key,
                           'terminate_when_done':terminate_when_done,
                           'stop_when_done':stop_when_done
                           }
         test_nodes.append(node_config)
         
         target.node_config = node_config
         target.instances = []
         return target
     return test_decorator
 
def deploy_app_nodes():
    for node in app_nodes:
        print "Deploying (app) %i instances of %s tags:%s" % (node['instances'],node['ami'],node['tags'])
        deploy_node(node,app_node_instances)
        
def deploy_test_nodes():
    for node in test_nodes:
        print "Deploying (test) %i instances of %s tags:%s" % (node['instances'],node['ami'],node['tags'])
        deploy_node(node,test_node_instances)

def import_test_output():
    outdir = "%soutput" % output_prefix
    print "Will save output to: %s" % outdir
    if not os.path.isdir(outdir):
        local("mkdir %s" % outdir)
    oid = exp_time
    local("mkdir %s/%s" % (outdir,oid))
    
    i = 0
    for node in test_node_instances:
            with settings(host_string=("%s@%s" % (node['user'],node['host'])),
                          key_filename=node['ssh_key'],
                          disable_known_hosts=True):
                outputid = "%s-%i" % (node['name'],i) 
                local("mkdir %s/%s/%s" % (outdir,oid,outputid))
                import_output(outputid,"output","%s/%s/%s/" % (outdir,oid,outputid))
                i = i + 1

def deploy_node(node,running):        
        config = node['config']
        
        for i in range(0,node['instances']):
            node_state = {'state':'launching','name':config.__name__}
            node_state.update(node)
            config.instances.append(node_state)
            
            launched = deploy(node['ami'],
                              node['key_pair'],
                              node['user'],
                              node['user_data'],
                              node['type'],
                              node['security_group'])
        
            host = launched[0]['host']
            id = launched[0]['id']
            node_state['host'] = host
            node_state['id'] = id
            
            print "Setting up host: %s" % node_state['host']
            

            with settings(host_string=launched[0]['host_string'],
                          key_filename=node['ssh_key'],
                          disable_known_hosts=True):
                ensure_up()
                
                node_state['state'] ='configuring'
                
                run("mkdir -p output")
                copy_local_files(node_state)
                
                config(node_state)
                node_state['state'] = 'ready'
                
                running.append(node_state)
                print "Deployed: %s" % node_state

def copy_local_files(node_state):
    host = node_state['host']
    node_name = node_state['config'].__name__
    try:
         if os.path.isdir(node_name):
              print "Copying local files from %s to %s" % (node_name,host)
              import_files(node_name,".")
         else:
              print "No local files found for %s" % node_name
    except IOError:
         print "Error copying local files %s to %s" % (node_name,host)        
        
def s3_get(bucket,key,target):
    run("sudo s3cmd --no-check-md5 --no-progress get s3://%s/%s /%s" % (bucket,key,target))    
            
def import_files_from_s3_as_tar(bucket,key,target):
    install_s3cmd()
    s3_get(bucket,key,target)
    run("tar -xvf %s" % target)
    
def import_files_from_s3_as_zip(bucket,key,target):
    install_s3cmd()
    s3_get(bucket,key,target)
    run("unzip %s" % target)
    
def import_files(localpath,remotepath):
    tf = str(uuid.uuid4())
    local("tar -czf %s.tar %s" %(tf,localpath))
    put("%s.tar" % tf,remotepath)
    run("tar -xvf %s/%s.tar" % (remotepath,tf))
    local("rm -rf %s.tar" % tf)
    
def import_output(outputid,remotepath,localpath):
    run("tar -cvf %s.tar %s" % (outputid,remotepath))
    get("%s.tar" % outputid,localpath)
    untar_output(localpath)
    #local("tar -xvf %s/%s.tar" % (localpath,outputid))
    #local("rm -rf %s/%s.tar" % (localpath,outputid))
    run("rm -rf %s.tar" % (outputid))
    

def splitpath(path, maxdepth=20):
     ( head, tail ) = os.path.split(path)
     return splitpath(head, maxdepth - 1) + [ tail ] \
         if maxdepth and head and head != path \
         else [ head or tail ]
         
def key_func(path):
    els = splitpath(path)
    for el in els:
        if el.endswith('-output'):
            return el[0:-7]
    return els[0]    

def file_acceptor(files,date):
    def acceptor(f):
        pathparts = splitpath(f)
        return [file for file in files if f.endswith(file)] and date in pathparts
    return acceptor     

def combine_csv(files,exptime=exp_time,root='.',get_key=key_func):
    return combine(root,file_acceptor(files,exptime),get_key)

def combine(root,accepts,get_key):
    all = []
    for dirpath, dnames, fnames in os.walk(root):
        
        for f in fnames:
            fp = os.path.join(dirpath, f)
    
            if accepts(fp):    
                key = get_key(fp)
                data = csv_to_dicts(fp)
                for row in data['rows']:
                    row.insert(0,key)
                    all.append(row)
    return all

def csv_to_dicts(file,hasheader=False):
    ifile  = open(file, "rb")
    reader = csv.reader(ifile)
     
    rownum = 0
    data = {'rows':[],'header':[]}
    
    for row in reader:
        # Save header row.
        if rownum == 0 and hasheader:
            header = row
            data['header'] = header
        else:
            if hasheader: rowobj = {}
            else: rowobj = []
            
            colnum = 0
            for col in row:
                if hasheader:
                    key = header[colnum]
                    rowobj[key] = col
                    rowobj[colnum] = col
                else:
                    rowobj.append(col) 
                
                colnum += 1
            data['rows'].append(rowobj)     
        rownum += 1
 
    ifile.close()
    return data
    
def untar_output(root):
    tars = []
    for dirpath, dnames, fnames in os.walk(root):
        for f in fnames:
            if f.endswith(".tar"):
                tars.append(os.path.join(dirpath, f))
    
    for tar in tars:
        local("tar -xvf %s -C %s" % (tar,os.path.dirname(tar)))  
    
def wait_for(nodes,check):
    for node in nodes:
        while not check(node):
            time.sleep(5)

def wait_until_up(nodes):
    def check(node):
        return node['state'] == 'ready'
    wait_for(check,nodes)

def wait_for_http(urls):
    print 'Checking http to urls: %s' % urls
    
    def check(url):
        print 'Checking http to %s' % url
        try:
            r = requests.get(url)
            return r.status_code == 200
        except:
            print 'Http to %s is not up yet' % url
            return False
        
    wait_for(urls,check)

def setup(target=[]):
    if not target:
        deploy_app_nodes()
        deploy_test_nodes()
        import_test_output()
    else:
        print "Invoke: %s" % target
        m = __import__ ('fabfile')
        func = getattr(m,target)
        func({})

def terminate_instances(instances):
    ids = ids = [n['id'] for n in instances]
    print "Terminating %s" % ids
    conn.terminate_instances(instance_ids=ids)
    
def stop_instances(instances):
    ids = ids = [n['id'] for n in instances]
    print "Stopping %s" % ids
    conn.stop_instances(instance_ids=ids)
    
def cleanup_node(node):
    if node['terminate_when_done']:
        terminate_instances(node['config'].instances)
    elif node['stop_when_done']:
        stop_instances(node['config'].instances)   
    node['config'].instances = []
    
def teardown():
    global test_node_instances
    global app_node_instances
    test_node_instances = []
    app_node_instances = []
    
    for node in app_nodes:
        cleanup_node(node)
        
    for node in test_nodes:
        cleanup_node(node)

def run_experiment(name,with_nodes=None):
    global output_prefix
    global exp_time
    output_prefix = "%s-" % name
    print "#####################################################"
    print "Launching experiment [%s]" % name
    print "#####################################################"
    start = time.time()
    setup()
    teardown()
    end = time.time()
    print "#####################################################"
    print "Experiment complete [%s]" % name
    print (end - start)
    print "#####################################################"
