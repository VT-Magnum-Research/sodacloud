Msg = function(props){
    this.id = uuid.v4();
	_.extend(this,props);
};

MsgBus = function(){
    this.subscribers = [];
}

MsgBus.prototype.subscribe = function(hdlr) {
	this.subscribers.push(hdlr);
};

MsgBus.prototype.unsubscribe = function(hdlr) {
     this.subscribers.splice(this.subscribers.indexOf(hdlr),1);   
};

MsgBus.prototype.publish = function(evt) {
	for ( var i in this.subscribers) {
		this.subscribers[i](evt);
	}
};

ObjRef = function(host, name, interface) {
    this.host = host;
	this.uri = host + "#" + name;
	this.name = name;
    this.interface = interface;
    this.type = "ObjRef";
};

ObjInvoker = function(msgbus) {
    msgbus.subscribe(_.bind(this.onEvent,this));
};

ObjInvoker.prototype.onEvent = function(evt){
    console.log("ObjInvoker checking evt: "+evt.type);
    if(evt.type == "invocation"){
        console.log("prepping invocation");
        
        var uri = evt.uri;
        var func = evt.method;
        var params = evt.parameters;
        Soda.namingSvc.get(uri,function(target){
             console.log('dispatching invocation ['+uri+"("+target+")."+func+"("+JSON.stringify(params)+")]");
         	 try{
         		 
         		 for(var i in params){
         			 params[i] = Soda.proxyIfNeeded(params[i]);
         		 }
         		 
	             var reply = new Msg({
	        		type : "response",
	        		responseTo : evt.id,
	        		destination : evt.source,
	        		result: null
	        	 });
	            
	             try{
	            	 if(target != null){
	            		 reply.result = target[func].apply(target,params);
	            	 }   
	             }catch(ex){
	            	 reply.exception = ex;
	             }
	             
	             Soda.send(reply);
         	 }catch(ex){
         		 console.log(ex);
         	 }
        });

    }
};

NamingSvc = function(parent){
    this.parent = parent;   
    this.objects = {};
    this.objRefs = {};
}

NamingSvc.prototype.publish = function(obj){
    var ref = null;
    
    if(obj in this.objRefs){
        ref = this.objRefs[obj];
    }
    else {
        var name = uuid.v4();
        ref = this.publishWithName(obj,name);
    }
    return ref;
}

NamingSvc.prototype.publishWithName = function(obj,name){
    var ref = new ObjRef(Soda.localAddress, name, []);
    this.objects[name] = obj;
    this.objects[ref.uri] = obj;
    this.objRefs[obj] = ref;
    return ref;
}

NamingSvc.prototype.get = function(name,callback){
    var obj = this.getLocal(name);
    if(obj != null){ callback(obj); }
    else if(this.parent != null){
        this.parent.get(name,_.bind(function(obj){
            if(obj){ this.objects[name] = obj; }
            if(callback && _.isFunction(callback)){callback(obj);} 
        },this));   
    }
}

NamingSvc.prototype.getLocal = function(name){
    var obj = null;
    if(name in this.objects){
         obj = this.objects[name];
    }
    return obj;
}

ReplyCatcher = function(replyId,msgbus,hdlr){
    this.replyId = replyId;   
    this.hdlr = hdlr;
    this.boundOnEvent = _.bind(this.onEvent,this);
    this.msgBus = msgbus;
    this.msgBus.subscribe(this.boundOnEvent);
};

ReplyCatcher.prototype.onEvent = function(evt){
    //console.log('checking '+evt.responseTo+" == "+this.replyId);
    if(evt.responseTo == this.replyId){
        console.log('got reply to:'+this.replyId);
        this.msgBus.unsubscribe(this.boundOnEvent);
        var rslt = (evt.result)? evt.result : evt.exception;
        
        if(rslt && rslt.type == "ObjRef"){
             rslt = new SodaSvc(rslt);   
        }
        
        if(this.hdlr && _.isFunction(this.hdlr)){
            console.log("dispatching result to handler : "+this.hdlr);
            this.hdlr(rslt)
        };
    }
};

SodaSvc = function(objref,name) {
    this.svcName = (name)? name : "Proxy["+objref.uri+"]";
	this.objRef = objref;
    
    if(objref.interface){
        var template = objref.interface;
        for(var i in template){
            var parts = template[i].split("/");
            var mname = parts[0];
            var argc = parts[1];
            this[mname] = this.proxyMethod(mname,argc);
        };
    }
};

SodaSvc.prototype.proxyMethod = function(name,argc){
    return _.bind(function(){
        
        var args = [];
        for(var i = 0; i < argc; i++){
            var arg = this.filterOutboundArg(arguments[i]);
            args.push(arg);
        }
        var hdlr = (arguments.length > argc && _.isFunction(arguments[argc]))? arguments[argc] : function(){};
        
        console.log("invoking ["+this.svcName+"."+name+"("+JSON.stringify(args)+")] with hdlr:"+hdlr);
        this.call(name,args,hdlr);
    },this);
}

SodaSvc.prototype.filterOutboundArg = function(arg) {

    if(_.isObject(arg)){
        var funcs = _.functions(arg);
        if(funcs.length > 0){
             arg = Soda.namingSvc.publish(arg);   
        }
    }
    
    return arg;
};

SodaSvc.prototype.call = function(method, params, hdlr) {

	var msg = new Msg({
		type : "invocation",
		uri : this.objRef.uri,
		method : method,
		parameters : params,
		destination : this.objRef.host
	});
    
    var replyCatcher = new ReplyCatcher(msg.id,Soda.msgBus,hdlr);

	Soda.send(msg);
};

SodaBase = function() {
	this.META_ADDRESS = "soda://meta";
	this.localAddress = "soda://" + uuid.v4();
	this.session = null;
	this.msgBus = new MsgBus();
    this.objInvoker = new ObjInvoker(this.msgBus);
};

SodaBase.prototype.proxyIfNeeded = function(obj) {
	return this.toProxy(obj);
}

SodaBase.prototype.toProxy = function(obj) {
   if(obj && obj.type == "ObjRef"){
        obj = new SodaSvc(obj);   
   }
   return obj;
}

SodaBase.prototype.createObjRef = function(uri, name) {
	return new ObjRef(uri, name);
};

SodaBase.prototype.createLocalObjRef = function(name) {
	return this.createObjRef(this.localAddress, name);
};

SodaBase.prototype.send = function(msg) {
	msg.source = this.localAddress;
	var cont = {
		destination : msg.destination,
		msg : JSON.stringify(msg)
	};

	this.session.publish(cont.destination, cont, true);
    
    console.log("sent:"+JSON.stringify(cont));
};

SodaBase.prototype.onConnect = function(session) {
	this.session = session;
	console.log('soda connected: ' + session);
    console.log('local addr:'+this.localAddress);
	session.subscribe(this.localAddress, _.bind(this.onEvent,this));

    var srvrNaming = new SodaSvc(new ObjRef(this.META_ADDRESS,"naming",["get/1"]),"NamingService");
    this.namingSvc = new NamingSvc(srvrNaming);
    //this.namingSvc = new SodaSvc(new ObjRef(this.META_ADDRESS,"naming",["get/1"]),"NamingService");

    this.connectCallback();
};

SodaBase.prototype.onEvent = function(topic, evt) {
    evt = JSON.parse(evt.msg);
    console.log("recevied:"+JSON.stringify(evt));
	this.msgBus.publish(evt);
};

SodaBase.prototype.onDisconnect = function(code, reason) {
	this.session = null;
};

SodaBase.prototype.connect = function(server,callback){
    this.connectCallback = callback;
    ab.connect(server, _.bind(this.onConnect,this), _.bind(this.onDisconnect,this));
};

var Soda = new SodaBase();



