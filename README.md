# Arrow Pipe

Media buttons support for Google Play Music

Powered by Kotlin, Spring WebFlux and websockets

## Installation

##### Build backend application:

   `gradlew clean build`

##### Install chrome extension:
    
   * Go to Chrome Extension tab
   
   * Enable Developer mode
   
   * Press `Load unpacked` button and feed it `extension/src` folder
    
    
## Usage

   Execute jar from `backend/build/libs`
   
  `java -jar backend-0.0.1.jar`
        
   Go to [Google Play Music](https://play.google.com/music/)
   
   Enjoy your working prev and next media buttons


#### TODO

   * icon
   * figure out websockets keepalive
   * lazy connect to server
   * publish it
