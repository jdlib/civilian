Civilian Framework
====

**Website:** http://www.civilian-framework.org/

**Documentation:** http://www.civilian-framework.org/doc.html

**Javadoc** http://www.civilian-framework.org/javadoc/index.html

Civilian is a server-side framework to develop web applications. It is written in Java (7+) 
and runs in any servlet (3.0+) container. It is published as open source and free to use. 

### Building

You need a Java JDK 8+, Apache Ant 1.6.0+ and Apache Ivy.<br/>
Unpack the distribution archive in an directory, open a console in
that directory and run

    ant resolve
    
This downloads thirdparty libraries with Ivy. Then run

    ant dist

to build the distribution in the <code>tmp</code> subdirectory.

### Running the samples

To run the samples applications bundled with Civilian call  

    ant jetty

This starts an embedded Jetty server and deploys the samples in it.
The samples start page is now available under http://localhost:8080/

You can also deploy the samples war from the build subdirectory <code>tmp</code>
in any Servlet 3.0+ container.
