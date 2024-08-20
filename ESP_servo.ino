#include <WiFi.h>
#include <ESP32Servo.h>

const char* ssid = "Mobitel 4G";
const char* password = "Pramuditha.1215";

WiFiServer server(80);

Servo myservo;  // create servo object to control a servo

int servoPin = 13; // GPIO pin to which the servo is connected

void setup() {
  Serial.begin(115200);
  
  // Connect to Wi-Fi
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected.");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  
  server.begin();

  myservo.attach(servoPin);  // attaches the servo on pin 13 to the servo object
}

void loop() {
  WiFiClient client = server.available();   // listen for incoming clients

  if (client) {                             // if you get a client,
    Serial.println("New Client.");          // print a message out the serial port
    String currentLine = "";                // make a String to hold incoming data from the client
    while (client.connected()) {            // loop while the client's connected
      if (client.available()) {             // if there's bytes to read from the client,
        char c = client.read();             // read a byte, then
        Serial.write(c);                    // print it out the serial monitor
        if (c == '\n') {                    // if the byte is a newline character
          // Check if the currentLine is blank, you got two newline characters in a row.
          // that's the end of the client HTTP request, so send a response:
          if (currentLine.length() == 0) {
            // HTTP headers always start with a response code (e.g. HTTP/1.1 200 OK)
            // and a content-type so the client knows what's coming, then a blank line:
            client.println("HTTP/1.1 200 OK");
            client.println("Content-type:text/html");
            client.println();
            client.println("<!DOCTYPE HTML><html>");
            client.println("<head><title>ESP32 Servo Control</title></head>");
            client.println("<body><h1>Servo Control</h1>");
            client.println("<button onclick=\"location.href='/left'\" type=\"button\">Left</button>");
            client.println("<button onclick=\"location.href='/right'\" type=\"button\">Right</button>");
            client.println("</body></html>");
            client.println();
            break;
          } else { // if you got a newline, then clear currentLine:
            currentLine = "";
          }
        } else if (c != '\r') {  // if you got anything else but a carriage return character,
          currentLine += c;      // add it to the end of the currentLine
        }

        // Check to see if the request is to control the servo
        if (currentLine.endsWith("GET /left ")) {
          myservo.write(0); // rotate servo to 0 degrees
        } else if (currentLine.endsWith("GET /right ")) {
          myservo.write(180); // rotate servo to 180 degrees
        }
      }
    }
    // close the connection:
    client.stop();
    Serial.println("Client Disconnected.");
  }
}
