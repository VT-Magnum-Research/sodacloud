
app_nodes = []
test_nodes = []

def app_node(ami,type="t1.micro",instances=1,user="ubuntu",tags=[]):
 
     def decorator(target):
         node_config =  {
                           'config':target,
                           'ami':ami,
                           'instances':instances,
                           'tags':tags,
                           'user':user
                           }
         app_nodes.append(node_config)
         target.node_config = node_config
         target.instances = []
         return target
     return decorator
 
def test_node(ami,type="t1.micro",instances=1,user="ubuntu",is_local=True,tags=[]):
 
     def test_decorator(target):
         test_nodes.append({
                           'config':target,
                           'ami':ami,
                           'instances':instances,
                           'tags':tags,
                           'user':user
                           })
         return target
     return test_decorator
 
def deploy_app_nodes():
    for node in app_nodes:
        print "Deploying %i instances of %s tags:%s" % (node['instances'],node['ami'],node['tags'])
        
        config = node['config']
        
        for i in range(0,node['instances']):
            node_state = {}
            node_state.update(node)
            config.instances.append(node_state)
        
            config(node_state)
            print "Deployed: %s" % node_state
    
    

@app_node(ami="ami123",instances=4)
def petstore_node(node_state):
    run("sudo apt-get install docker")
    run("sudo apt-get install s3cmd")
    id = run("docker run -i -t -d -p 9966 petstore mvn -f petstore/pom.xml tomcat7:run")
    port = run("docker port %s 9966" % id)
    
    node_state['docker.id'] = id
    node_state['docker.port'] = port
    
@test_node(ami="ami234")
def load_generator():
    wait_for_nodes(pet_store_node)
    ensure_http(pet)

def run(arg):
    print arg;
    

    
    
deploy_app_nodes()
    