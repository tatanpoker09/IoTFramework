# IoTFramework
IoT Framework is a framework to prove you can unify technologies, making IoT development a whole lot easier. It aims to be the best single-technology framework implementation.

## Usage
IoTFramework is not yet available as a Gradle artifact, therefore it is necessary to download the source code and import it into your project. You may either download the source code as a Zip through Github, or you may clone it with:

git clone https://github.com/tatanpoker09/IoTFramework.git

The Framework itself is located in the Framework module. The other modules contain example applications you can use as a reference.

Once you have downloaded the code, you may either create a new module from scratch for your application, or import the app module into an existing project.

Next, from your project-level gradle file, add IoTFramework dependency:
```
dependencies {
    // Change 'app' to whatever name you choose for IoTFramework's module
    compile project(":app")
    [...]
}
```
In order to create an App with the Framework, you have to register the devices that will be able to connect. To do this you have to extend from NetworkComponent in each device.
For example, to create a Camera and an Alarm you would create a Camera.class with:
```java
@Device(id = ALARM_ID, layout = R.layout.alarm_layout)
public class Camera extends NetworkComponent {

    public Camera(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }
}
```
And an Alarm.class with:
```java
@Device(id = ALARM_ID, layout = R.layout.alarm_layout)
public class Alarm extends NetworkComponent {

    public Alarm(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }
}
```
And then you'll have to register it to a Device Manager. To do this simply create a new class, extend from TreeDeviceManager and annotate it as DeviceManager. You must also annotate the device that will be considered "Local" at the time of deployment.

```java
@DeviceManager()
public abstract class Devices extends TreeDeviceManager {
    @Local
    public abstract Camera getCamera();


    public abstract Alarm getAlarm();
}

```

## Features
### Powerful Annotation Processing

### Star Topography

### Easy Packet System.
### Brocadcasting
