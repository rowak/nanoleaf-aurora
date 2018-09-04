# Nanoleaf Aurora Java API
A simple java wrapper for the Nanoleaf Aurora RESTful API that fully supports all features of the official API, but adds additional helper methods and classes to make your life easier.

## [Documentation](https://htmlpreview.github.io/?https://github.com/rowak/nanoleaf-aurora/blob/master/doc/index.html)

## Installation
### Maven
Simply add the following dependency to your ```pom.xml``` file in your project.
```xml
<dependency>
  <groupId>io.github.rowak</groupId>
  <artifactId>nanoleaf-aurora</artifactId>
  <version>1.02</version>
</dependency>
```
### Manual
You can also download the [compilled jar](http://central.maven.org/maven2/io/github/rowak/nanoleaf-aurora/) and import it into your project.

## Connecting to the Aurora
First search for all of the Aurora devices connected to your local network, then select one from the returned list. Make sure to store the host name and port.
```Java
int timeout = 5000;
List<InetSocketAddress> auroras = Setup.findAuroras(timeout)
String host = auroras.get(0).getHostName();
int port = auroras.get(0).getPort();
```
Next define the API level (Currently "v1").
```Java
String apiLevel = "v1";
```
Then you can create an access token. (Note: You must physically hold down the power button on your Aurora controller before running the following code).
```Java
String accessToken = Setup.createAccessToken(host, port, apiLevel);
```
Finally you can create an ```Aurora``` object.
```Java
Aurora aurora = new Aurora(host, port, apiLevel, accessToken);
```

Note: You can also directly create a new ```Aurora``` object if you already have an access token and you know the host, port, and apiLevel of your Aurora.

## Controlling the Aurora
Once you have created an ```Aurora``` object, you can start using its methods.
### State
Contains basic methods used for getting and setting Aurora state information (brightness, color, etc). Below are a few examples.
#### On/Off
```Java
boolean isOn = aurora.state().getOn();  // returns true if the Arora is on and false if it is off
aurora.state().setOn(true);             // sets the on state of the Aurora
aurora.state().toggleOn();              // toggles the state of the Aurora (on -> off, off -> on)
```
#### Brightness
```Java
int brightness = aurora.state().getBrightness();  // returns the brightness of the Aurora
aurora.state().setBrightness(100);                // sets the brightness of the Aurora (max = 100, min = 0)
```
#### Hue
```Java
int hue = aurora.state().getHue();  // returns the hue of the aurora (solid effects only)
aurora.state().setHue(360);         // sets the hue of the aurora (max = 360, min = 0)
```
#### Saturation
```Java
int saturation = aurora.state().getSaturation();  // returns the saturation of the Aurora (solid effects only)
aurora.state().setSaturation(100);                // sets the saturation of the Aurora (max = 100, min = 0)
```
#### Color Temperature
```Java
int colorTemp = aurora.state().getColorTemperature()  // returns the color temperature in Kelvins of the Aurora (color temperature effects only)
aurora.state().setColorTemperature(4000);             // sets the color temperature of the Aurora (color temperature effects only)
```

### Effects
The API includes various effect methods for adding, removing, renaming, previewing, and getting effects from the Aurora. Below are a few examples, but refer to the [documentation](https://htmlpreview.github.io/?https://github.com/rowak/nanoleaf-aurora/blob/master/doc/index.html) for more information.
```Java
String currentEffect = aurora.effects().getCurrentEffectName();  // returns the name of the current effect
aurora.effects().setEffect(effectName);                          // sets the current effect to an existing effect
aurora.effects().addEffect(effect);                              // adds a new Effect object to the Aurora
aurora.effects().deleteEffect(effect);                           // removes an Effect object from the Aurora
aurora.effects().renameEffect(effectName, newName);              // renames an existing effect
aurora.effects().previewEffect(effect);                          // displays an effect on the Aurora, but does not install it
```

### Panel Layout
The panel layout methods are mostly getters used to get various information about the arrangement of the Aurora panels. Below are a few examples of these methods.
```Java
int numPanels = aurora.panelLayout(true).getNumPanels();  // returns the number of connected panels. Note: The Rhythm module counts as a panel by default. Use ```includeRhythm``` to choose whether to include this or not
int sideLength = aurora.panelLayout().getSideLength();    // returns the side length of each panel
Panel[] panels = aurora.panelLayout().getPositionData();  // returns an array of type Panel containing each connected Aurora panel
```

### Rhythm
The Rhythm aspect of the java API mostly contains getters for information such as mode, connected/not connected, active/not active, aux available, etc. Below are a few examples.
```Java
boolean connected = aurora.rhythm().getConnected();        // whether or not the Rhythm is connected to the Aurora
boolean active = aurora.rhythm().getActive();              // whether or not the Rhythm's microphone is currently active (blue led is on)
boolean auxAvailable = aurora.rhythm().getAuxAvailable();  // whether of not the aux (3.5mm) input is available
```
