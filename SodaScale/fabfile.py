from sodascale import *

petstore_instances = []

@experiment("peformance measurement")
def performance_measurement():
    types = ["c1.medium"]
    for type in types:
        petstore_node.node_config['type'] = type
        run_experiment("Benchmark-%s" % type)
#     data = combine_csv('stats.csv')
#     for row in data:
#         print row

















@app_node(ami="ami-3be88052",
           instances=1,
           type="m3.xlarge",
          security_group="docker-ec2",
          user='ubuntu',
          terminate_when_done=False,
          key_pair="soda")
def petstore_node(node_state):
    create_docker_image_from_s3(imgid='petstore',bucket='sodascale',key='springpetstore.tar')
    id = run("docker run -i -t -d -p 9966 petstore mvn -f /petstore/spring-petclinic/pom.xml tomcat7:run")
    port = run("docker port %s 9966" % id)
     
    node_state['docker.id'] = id
    node_state['docker.port'] = port
    node_state['petstore.url'] = "http://%s:%s" % (node_state['host'],port)
    print("http://%s:%s/petclinic" % (node_state['host'],port))
     
    global petstore_instance
    petstore_instance = node_state
    wait_for_http(["%s/petclinic" % node_state['petstore.url']])
    

@app_node(ami="ami-01037668",
          instances=1,
          type="m3.xlarge",
          security_group="docker-ec2",
          user='ubuntu',
          terminate_when_done=False,
          key_pair="soda")
def petstore_node(node_state):
    run("nohup mvn -f spring-petclinic/pom.xml tomcat7:run > output.log 2>&1 &", pty=False)
    node_state['petstore.url'] = "http://%s:%s" % (node_state['host'],9966)
    print("http://%s:%s/petclinic" % (node_state['host'],9966))
    global petstore_instances
    petstore_instances.append(node_state)
    #wait_for_http(["%s/petclinic" % node_state['petstore.url']])    
    

# @test_node(ami="ami-57147e3e",
#           instances=1,
#           security_group="docker-ec2",
#           user='ubuntu',
#           key_pair="soda")
# def load_generator(node_state):
#     global petstore_instance
#     url = petstore_instance['petstore.url']
#     urls = ["%s/petclinic" % url]
#     wait_for_http(urls)
#     
#     #start_load_capture('locust_load',[petstore_node])
#     
#     run("locust -H %s -c 200 -r 10 -n 1000 --no-web -f load_generator/locustfile.py" % url)
#     
#     #stop_load_capture([petstore_node])
#     
#     run("mv stats.csv output/")
#     run("mv distribution.stats.csv output/")
   
    