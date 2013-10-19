Sodacloud Documentation
=========
Motivation
------------
The immense mobile application market and its rapid growth have resulted in numerous developers publishing 
thousands of innovative applications for mobile platforms. However, the communication architecture for mobile 
applications still echoes the tradition where HTTP communication is combined with object-oriented programming.
The developers must consider data marshaling, threading, synchronization, and identity management which are all
time-consuming and error-prone.

Take Android as an example, usually there are two ways to do network communication: use Handler or AsyncTask. 

AsyncTask works like this:
```java  
public class AddReportTask extends AsyncTask<Void, Void, Void> {
	String fromServer = null;
	String content = null;

	public AddReportTask(String content){
		this.content = content;
	}
	@Override
	protected Void doInBackground(Void... params) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(Host.hostaddress + "addreport/");
   	
			try {
				HttpEntity myEntity = new StringEntity(content);
				request.setEntity(myEntity);

				HttpResponse response = httpclient.execute(request);

				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					while ((fromServer = rd.readLine()) != null) {
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
        status_.setText(fromServer);
	}
}
```
Handler works like this:
```java  
public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
             switch (msg.what) {                	
                  case ADD_REPORT:
                       status_.setText(msg.getData().getString(HANDLER_TAG));
                       break;
             }
             super.handleMessage(msg);
        }
};
......
Button_.setOnClickListener(new View.OnClickListener() {
	@Override
	public void onClick(View v) {	
		new Thread(new CheckUpdatesTask()).start();
	}
});
......
public class CheckUpdatesTask implements Runnable {
        String fromServer = null;
	public CheckUpdatesTask() {
	}

	public void run() {
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost request = new HttpPost(HOST_ADDRESS);
            HttpEntity myEntity = new StringEntity(id);
	    request.setEntity(myEntity);
            HttpResponse response = httpclient.execute(request);
            
	    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			BufferedReader rd = new BufferedReader(newInputStreamReader(response.getEntity().getContent()));
			fromServer = rd.readLine();		
			
			Message message = new Message();
			message.what = MainActivity.ADD_REPORT;
							
			Bundle bundle = new Bundle();
			bundle.putString(HANDLER_TAG, fromServer);
			message.setData(bundle);
							
			myHandler.sendMessage(message);
            	}
	}
}
```
The problem with above approach is:

1. You cannot pass object through HTTP directly. Each time you want to send an object, you need to marshall the 
object to some separate primitive and when you receive it, you need to assemble them again.

2. Each time you try to create a different kind of request, you need to create an AsyncTask or Handler for it. 
The lines of code increase rapidly as the program becomes complex.

3. If you need to have multiple AsyncTask running at the same time and update the UI, you have to be cautious since 
the things will get complicated and error-prone. Actually the behavior of AsyncTask has changed several
times during the revolution of Android. When first introduced, AsyncTask were executed serially on a single 
background thread. Starting with DONUT(1.6), this was changed to a pool of threads allowing multiple tasks to 
operate in parallel. Starting with HONEYCOMB(3.1), tasks are executed on a single thread to avoid common application
errors caused by parallel execution. The inconsistency adds burdens to developers who wish to distribute their 
apps to multiple platforms.

We propose an object-oriented communication framework, SodaCloud, which hides the complexity of low-level 
network communication and allows developers to focus solely on business logic. We implement a distributed 
object middleware that supports 2-way object-oriented communication, eliminating the transition between 
object-oriented programming and non-OO, request-response style communication.

For a complete comparison, see the implementation of an observer pattern in HTTP([client][httpclient],[server][httpserver]) 
and SodaCloud ([client][sodaclient],[server][sodaserver])).

[httpclient]: https://github.com/VT-Magnum-Research/sodacloud/tree/master/Examples/HttpClient
[httpserver]: https://github.com/VT-Magnum-Research/sodacloud/tree/master/Examples/HttpServer
[sodaclient]: https://github.com/VT-Magnum-Research/sodacloud/tree/master/Examples/SodaClient
[sodaserver]: https://github.com/VT-Magnum-Research/sodacloud/tree/master/Examples/SodaServer


A simple example with the Observer pattern 
------------

Observer pattern is a design pattern  that is widely used in mobile development because it describes the 
very behavior of a large proportion of mobile applications. We use the observer pattern to illustrate the convenience 
SodaCloud brings.

In the following example, “MaintenanceReports” is an observable object on the server and there is MaintenanceListener 
on the client as observer.
In a simple user scenario, the user wants to add a new report in the client side and the server side needs 
to handle this.

- In Java server-side: 

Class “MaintenanceReports” is declared. It stores the reports and listeners for the reports in lists. When a new
report is uploaded, it will be added to the list of reports and all the listeners will be notified.
  
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

- In Java client-side (Android), a MaintenancesListener interface is declared. Two method is defined for the interface to handle
the add and change event of th report.

```java
public interface MaintenanceListener {
	@SodaAsync
	public void reportAdded(MaintenanceReport r);
	@SodaAsync
	public void reportchanged(MaintenanceReport r);
}
```

AndroidSoda.async() is the method to call when you need to invoke 
network connection with server. A new thread will be created. The reportHandle is fetched from server using SVC naming service.
A listener is implemented and then added to the handle. Remember to annotate with @SodaInvokeInUi if you try to make any 
changes to UI elements. In the end, addReport() method is called to add the new report to the server.

```java  
AndroidSoda.async(new Runnable() {
	@Override
	public void run() {
		MaintenanceReports reportHandle = as.get(MaintenanceReports.class, MaintenanceReports.SVC_NAME);
		reportHandle.addListener(new MaintenanceListener() {
			@SodaInvokeInUi
			public void reportAdded(final MaintenanceReport r) {
				Toast.makeText(CreateReportFragment.this.getActivity(),"New report:" + r.getContents(),
						Toast.LENGTH_SHORT).show();
			}
			@Override
			public void reportchanged(final MaintenanceReport r) {
			}
		});
		reportHandle.addReport(r);
	}
}
```

Introduction of SodaCloud
------------
SodaCloud is a shared object distribution architecture for cloud systems. 
SodaCloud provides a platform that automatically creates mobile/cloud applications with optimized communications,
cloud-based data sharding, cyber-physical information usage (e.g. geo-located data), 
and code generation / testing methods for ensuring platform correctness. 

Using SodaCloud, the developer will be able to build a model of the client/server services, data, and 
security requirements and automatically generate an entire system backbone. The backbone will allow developers 
to enter key system logic manually, while the backbone will take care of client/cloud communications, 
automatic platform instrumentation and replay of request data for QA, data marshaling infrastructure, 
and capture of key performance / cloud resource sizing data to aid in resource allocation decisions and 
operations cost optimization.

![Soda L](Docs/images/architecture.PNG "Soda")

SodaCloud supports automatic creation of mobile client to server communication infrastructure,
including data marshaling mechanisms, that alleviate the need for developers to hand-code complex 
asynchronous communication pathways and optimize underlying protocols for the target API. 

SodaCloud is built on top of an HTTP + push messaging communication system. A key challenge with HTTP-based 
communication from apps is the API-boundaries that transition from traditional OO-based programming to high-latency, 
non-OO, request-response style communication. SodaCloud builds an OO-based abstraction on top of this communication 
pathway to hide these complexities from developers and simplify testing. 

Moreover, when using HTTP-based communication approaches, they are biased towards client-initiated communication
and make server-to-device pushing of data challenging. Other mechanisms for pushing data to devices, 
such as Android’s C2DM push messaging are available, but they required complex authentication and negotiation 
between both the client and server and the server and third-party messaging servers. Further, these push notification
systems have multiple asynchronous operations that must complete and state management requirements that 
add another layer of complexity on communications. SodaCloud provides a seamless abstraction
on top of both HTTP and these push messaging mechanisms to provide simplified two-way client/server interaction 
initiation.

![Soda L](Docs/images/layer.png "Soda")


- Javascript client-side

Overview of supported platforms
------------
Android, Javascript, iOS.

Setting up a Java server-side project
------------
1.Download SodaCloud and SodaCloudJetty project. SodaCloud is the fundamental library project for all the marshalling 
and abstraction logic. SodaCloudJetty is a built upon Jetty which provides HTTP server and servlet container.

2.Create a new Java project in eclipse. Right click on the project and click on the `properites`. Select Java Build Path -> Projects 
and add `SodaCloud` and `SodaCloudJetty` to the build path.

3.To create a basic server with Jetty. Create the main class implements ServerSodaListener interface. The started() will
be called when the server is launched.

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
4.Defining interfaces for remoteable objects

Here we define the manage interface “MaintenanceReports”.
A unique string(SVC_NAME) needs to be defined for the object. It can be used later for looking up objects with naming service.
```java
public interface MaintenanceReports {
		public static final String SVC_NAME = "maintenance";
		public void addReport(MaintenanceReport r);
		public void modifyReport(MaintenanceReport r);
	  	public void deleteReport(UUID id);
……
}
```
5.How to bind objects

To support reference of objects between client and server, a unique identity of the object is needed for looking up.
In SodaCloud, this is achieved by a naming service mechanism where an object is bind to SVC_NAME.

To bind an object, call the function soda.bind(object, SVC_NAME); 
Where the first parameter is the object to be bind and the second parameter is the String of its SVC name.

```java
soda.bind(reports, MaintenanceReports.SVC_NAME);
```

6.Java implementation limitations

a.	In Java, only parameters that are of an interface type can be
passed by reference.

b.	In Java, generic collections are not supported for incoming pass by
value types because Java does not have reified generic types.

Setting up an Android project
------------

1.Create a new Android project in eclipse. Right click on the project and click on the `properites`. Select Java Build Path -> Projects 
and add project `SodaCloud` to the build path. Select `Android` to add `SodaCloudAndroid` to library.

2.Add `"android.permission.INTERNET"` as user permission in AndroidManifest.xml file.

3.Initializing AndroidSoda and connecting

a)Instead of sending HTTP request every time, SodaCloud uses object AndroidSoda to manage the connection with the server.
The activity will need to implement AndroidSodaListener interface and override the connected() method. 
Then AndroidSoda.init() will call the connected() method and return the AndroidSoda object. After that, 
you can use AndroidSoda for any connection with the server.

```java
   public class MainActivity extends Activity implements AndroidSodaListener {
      private AndroidSoda as_;
      private AndroidSodaListener asl_;
      @Override
      protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   asl_ = this;
           AndroidSoda.init(ctx_, mHost, 8081, asl_);
      }
      @Override
      public void connected(AndroidSoda s) {
	   this.as_ = s;
      }
     
}
```

4.Looking up objects on the server with the naming service

To start a query without blocking the UI, call AndroidSoda.async() method. It will start a new thread for the network 
connection. The lookup is by calling the get() method of AndroidSoda. It will return the objects in server
with the SVC_NAME you have bind previously.

```java
 		AndroidSoda.async(new Runnable() {
		  @Override
		  public void run() {
			    reports_ = as.get(MaintenanceReports.class,MaintenanceReports.SVC_NAME);
      		  }
    }
```
5.Defining interfaces for remote objects

6.Soda threading architecture

When the AndroidSoda is initialized, a new thread is created. The network operation is conducted in the new thread. 
A callback is passed as parameter which handles the result of the network communication. 
The update of UI after network communication is always done on UI thread of Android. 
his is ensured by using @SodaInvokeInUi annotation.

7.Use of the Soda annotations for interactions with the UI thread

@SodaInvokeInUi
This annotation marks methods that should always be invoked by Soda in the context of the UI thread on Android. 
If you apply this annotation, you cannot invoke any blocking Soda methods 
(e.g. any Soda methods that are not void and annotated with @SodaAsync) inside of the method or 
Android will freak out for doing network ops in the gui thread.

8.Use of the Soda annotations for pass by reference and pass by value

@SodaByValue 
This annotation marks classes that should be passed by value rather than object reference.

9.Sharing object references via QR code

To share an object via QR code, you need to bind the QR code context to the object. 
SodaQR is created from a QR image and bind to Soda object.

```java
public void bindQRContext(Soda s, MaintenanceReport r) {
		SodaQR qr = SodaQR.create(r.getContents());
		s.bind(r).to(qr);
	}
```
To lookup object based on QR code, first build the SodaQR object from the image byte[] with fromImageData() method.
Then use soda.find() method where the first parameter is the class type and 
second parameter is SodaContext. The returned SodaQuery is passed in to callback for asynchronized running.

```java
SodaQR _objQR = SodaQR.fromImageData(captured_image);
SodaQuery<MaintenanceReport> _objSQ = s.find(MaintenanceReport.class,_objQR);
callback.handle(_objSQ.getList_());
```

Setting up a Javascript project
------------
1.Importing soda.js

2.Connecting to the server

3.Looking up objects on the server with the naming service

4.Constructing objects that are remoteable

5.How remote object proxies are created

6.Invoking remote methods and transparent creation of object
references for local objects

7.Rules for pass by value vs. pass by reference (e.g. it has a
function as a attribute)

8.Javascript implementation limitations

a)	In Javascript, all objects with a function are automatically passed
by reference.

b)	In Javascript, pass by reference is only supported for top-level
method parameters. If a pass by value object is provided to a method,
deep introspection will NOT be used to find and convert embedded
objects to ObjRefs.


Setting up an Objective-C project
------------

1.Adding SodaCloud to your XCode project

2.no-arc and other build settings with cocoapods

3.Initializing Soda

```
    Soda* soda = [[Soda alloc]init];
    [soda connect:host withListener:self];
```    

4.Defining interfaces for remotable objects with SODA_METHODS macros

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
5.Rules for pass by reference vs pass by value with REF(...)

6.Looking up remote objects with the naming service

```
id obj = [soda.namingService get:@"maintenance" asType:[MaintenanceReports class]];
```
7.Soda threading architecture with Grand Central Dispatch

8.How NSProxies are created

[![Build Status](https://buildhive.cloudbees.com/job/VT-Magnum-Research/job/sodacloud/badge/icon)](https://buildhive.cloudbees.com/job/VT-Magnum-Research/job/sodacloud/)
