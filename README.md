Sodacloud Documentation
=========

Overview of SodaCloud
------------
- SodaCloud is a shared object distribution architecture for cloud systems. SodaCloud provides a platform that automatically creates mobile/cloud applications with optimized communications, cloud-based data sharding, cyber-physical information usage (e.g. geo-located data), and code generation / testing methods for ensuring platform correctness. 

- Using SodaCloud, the developer will be able to build a model of the client/server services, data, and security requirements and automatically generate an entire system backbone. The backbone will allow developers to enter key system logic manually, while the backbone will take care of client/cloud communications, automatic platform instrumentation and replay of request data for QA, data marshaling infrastructure, and capture of key performance / cloud resource sizing data to aid in resource allocation decisions and operations cost optimization.

A simple example with the Observer pattern 
------------

In the following example, we will use the object “MaintenanceReport” for illustration. In a simple user scenario, the user wants to add a new report in the client side and the server side needs to handle this.

- In Java server-side: a MaintenancesListener interface is declared:

```java
public interface MaintenanceListener {
	@SodaAsync
	public void reportAdded(MaintenanceReport r);
	@SodaAsync
	public void reportchanged(MaintenanceReport r);
}
```
A manager class “MaintenanceReports” is declared: 
  
```java  
public class MaintenanceReportsImpl implements MaintenanceReports {
	private List<MaintenanceListener> listeners_ = new LinkedList<MaintenanceListener>();
	private List<MaintenanceReport> reports_ = new LinkedList<MaintenanceReport>();
	@Override
	public void addReport(MaintenanceReport r) {
		reports_.add(r);
		for (MaintenanceListener l : listeners_) {
			l.reportAdded(r);
		}
	}
	……
@Override
	public void addListener(MaintenanceListener l) {
		listeners_.add(l);
	}
	……
}
```

- In Java client-side (Android), the reportHandle is fetched from server and a listener is implemented and added to the reportHande.

```java  
AndroidSoda.async(new Runnable() {
			@Override
			public void run() {
				MaintenanceReports reportHandle = as.get(
						MaintenanceReports.class, MaintenanceReports.SVC_NAME);
				reportHandle.addListener(new MaintenanceListener() {
					@SodaInvokeInUi
					public void reportAdded(final MaintenanceReport r) {
						Toast.makeText(CreateReportFragment.this.getActivity(),
								"New report:" + r.getContents(),
								Toast.LENGTH_SHORT).show();
					}
					@Override
					public void reportchanged(final MaintenanceReport r) {
					}
});
```
- Javascript client-side

Overview of supported platforms
------------
Android, Javascript, iOS.

Architecture of SodaCloud
------------

Setting up a Java server-side project
------------
- Download SodaCloud and SodaCloudJetty project.

- Create a Java project in eclipse and add SodaCloud and SodaCloudJetty to the build path.

- To create a basic server with Jetty. Create the main class implements ServerSodaListener interface.

```java 
   public class Server implements ServerSodaListener {
    	public static void main(String[] args) {
		      ServerSodaLauncher launcher = new ServerSodaLauncher();
		      launcher.launch(new NativeJavaProtocol(), 8081, new Server());
	    }
      @Override
    	public void started(Soda soda) {
	  }
}
```
- Defining interfaces for remoteable objects

Here we define the manage interface “MaintenanceReports”

```java
public interface MaintenanceReports {
		public static final String SVC_NAME = "maintenance";
		public void addReport(MaintenanceReport r);
		public void modifyReport(MaintenanceReport r);
	  public void deleteReport(UUID id);
……
}
```
-How to bind objects

To bind an object, call the function soda.bind(object, SVC_NAME); Where the first parameter is the object to be bind and the second parameter is the String of its SVC name.

```java
soda.bind(reports, MaintenanceReports.SVC_NAME);
```
- How dynamic proxies are created

After the call of soda.bind(), the proxies of the object are created automatically by Soda. The process happens in DefaultNamingService.java.

```java
ObjRef ref = new ObjRef("" + data.get("uri"),type.getName());
obj = (T)proxyFactory_.createProxy(new Class[]{type}, ref);
```

- NativeJavaProtocol vs. DefaultProtocol configuration

- Java implementation limitations

a.	In Java, only parameters that are of an interface type can be
passed by reference.

b.	In Java, generic collections are not supported for incoming pass by
value types because Java does not have reified generic types.

Setting up an Android project
------------

- Importing the Android library project

- Setting the correct permissions

- Initializing AndroidSoda and connecting

a)	The activity will need to implement AndroidSodaListener interface and override the connected() method. Then AndroidSoda.init() will call the connected method and return the AndroidSoda object. After that, you can use AndroidSoda for any connection with the server.

```java
   public class MainActivity extends Activity implements AndroidSodaListener {
      private AndroidSoda as_;
      private AndroidSodaListener asl_;
      @Override
	    protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
	       asl_ = this;
          AndroidSoda.init(ctx_, mHost, 8081, asl_);
          @Override
	     public void connected(AndroidSoda s) {
		        this.as_ = s;
      }
}
```

- Looking up objects on the server with the naming service

To start a query, call AndroidSoda.async() method:

```java
 		AndroidSoda.async(new Runnable() {
		  @Override
		  public void run() {
			    reports_ = as.get(MaintenanceReports.class,MaintenanceReports.SVC_NAME);
      }
    }
```
- Defining interfaces for remote objects

- Soda threading architecture

When the AndroidSoda is initialized, a new thread is created. The network operation is conducted in the new thread. A callback is passed as parameter which handles the result of the network communication. The update of UI after network communication is always done on UI thread of Android. This is ensured by using @SodaInvokeInUi annotation.

- Use of the Soda annotations for interactions with the UI thread

@SodaInvokeInUi
This annotation marks methods that should always be invoked by Soda in the context of the UI thread on Android. 
If you apply this annotation, you cannot invoke any blocking Soda methods (e.g. any Soda methods that are not void and annotated with @SodaAsync) inside of the method or Android will freak out for doing network ops in the gui thread.

- Use of the Soda annotations for pass by reference and pass by value

@SodaByValue 
This annotation marks classes that should be passed by value rather than object reference.

- Sharing object refs via QR code

To share an object via QR code, you need to bind the QR code context to the object. SodaQR is created from a QR image and bind to Soda object.

```java
public void bindQRContext(Soda s, MaintenanceReport r) {
		SodaQR qr = SodaQR.create(r.getContents());
		s.bind(r).to(qr);
	}
```
To lookup object based on QR code, use soda.find() method where the first parameter is the class type and second parameter is SodaContext. The returned SodaQuery is passed in to callback for asynchronized running.

```java
SodaQR _objQR = SodaQR.fromImageData(b);
SodaQuery<MaintenanceReport> _objSQ = s.find(MaintenanceReport.class,_objQR);
callback.handle(_objSQ.getList_());
```

- Android implementation limitations

Setting up a Javascript project
------------
- Importing soda.js

- Connecting to the server

- Looking up objects on the server with the naming service

- Constructing objects that are remoteable

- How remote object proxies are created

- Invoking remote methods and transparent creation of object
references for local objects

- Rules for pass by value vs. pass by reference (e.g. it has a
function as a attribute)

- Javascript implementation limitations

a)	In Javascript, all objects with a function are automatically passed
by reference.

b)	In Javascript, pass by reference is only supported for top-level
method parameters. If a pass by value object is provided to a method,
deep introspection will NOT be used to find and convert embedded
objects to ObjRefs.


Setting up an Objective-C project
------------

- Adding SodaCloud to your XCode project

- no-arc and other build settings with cocoapods

- Initializing Soda

```
    Soda* soda = [[Soda alloc]init];
    [soda connect:host withListener:self];
```    
- Defining interfaces for remotable objects with SODA_METHODS macros

Obj-C doesn't support runtime introspection of method parameter types,
so several macros are created to capture this info:
This defines a remote object's methods and says it has one method with
a void return type, called addListener, that takes an ObjRef to a
MaintenanceListener.
```
@interface MaintenanceReports : NSObject<SodaObject>
-(void)addListener:(MaintenanceListener*)listener;
@end
@implementation MaintenanceReports
    SODA_METHODS(
             SODA_VOID_METHOD(@"addListener",REF(MaintenanceListener))
    )
@end
```
- Rules for pass by reference vs pass by value with REF(...)

- Looking up remote objects with the naming service

```
id obj = [soda.namingService get:@"maintenance" asType:[MaintenanceReports class]];
```
- Soda threading architecture with Grand Central Dispatch

- How NSProxies are created

[![Build Status](https://buildhive.cloudbees.com/job/VT-Magnum-Research/job/sodacloud/badge/icon)](https://buildhive.cloudbees.com/job/VT-Magnum-Research/job/sodacloud/)
