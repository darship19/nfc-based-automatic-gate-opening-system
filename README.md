# NFC-Based Automatic Gate Opening System with Ultrasonic Vehicle Detection and Owner-Requested Access

## Project Overview

This project aims to design and implement an NFC-based automatic gate opening system that enhances convenience and security for residential vehicle parking areas. The system leverages modern technologies like NFC, ultrasonic sensors, and mobile app integration to provide an automated, secure, and user-friendly gate access solution.

## Problem Statement

Traditional gate opening systems often rely on manual labor or simple remote controls, leading to operational inefficiencies and security vulnerabilities. Our project addresses these issues by developing a smart gate opening system that automates access control while minimizing unauthorized entry and enhancing ease of use for residents.

## Features

- **NFC Technology**: Allows authorized users to effortlessly gain access by scanning their NFC-enabled devices.
- **Ultrasonic Sensors**: Detects the presence of vehicles near the gate, ensuring timely and accurate gate operation.
- **Owner-Requested Access**: Provides a mobile app for manual access requests, allowing flexibility and control over gate access.
- **QR Code System**: Handles emergency access and new user requests via QR code scanning.

## System Components and Interfacing
## User Interface

![User Interface](images/user-interface.jpeg)

![User Interface](images/user-interface1.png)



### Step 1: Vehicle Detection

- **Ultrasonic Sensor**
  - **Input**: Detects the presence of a vehicle near the gate.
  - **Output**: Sends a signal to the microcontroller indicating the vehicle's presence.

- **Microcontroller: ATMega328P**
  - **Inputs**: Receives signals from the ultrasonic sensor.
  - **Outputs**: Controls the LCD screen and communicates with the RFID reader.

### Step 2: User Interaction

- **LCD Screen**
  - **Input**: Displays prompts like "ADD CARD" when a vehicle is detected.
  - **Output**: Receives commands from the microcontroller to show appropriate messages.

- **RFID Reader**
  - **Input**: Scans RFID cards presented by users.
  - **Output**: Sends RFID card data to the microcontroller for access verification.

### Step 3: Access Control

- **RFID Card Data Processing**
  - **Input**: RFID card data received from the RFID reader.
  - **Output**: Verification result sent to the microcontroller to grant or deny access.

### Step 4: Emergency and Request Handling

- **QR Code System**
  - **Input**: Scans QR codes for emergency access or new user requests.
  - **Output**: Sends request data to the owner.User access his/her mobile for approve or reject the user request

- **Access Authorization**
  - **Input**: Data from RFID reader and QR code system.
  - **Output**: Commands to open the gate or deny access based on verification and request approval.

### Step 5: Gate Mechanism

- **Servo Motor**
  - **Input**: Receives command from the microcontroller to open or close the gate.
  - **Output**: Physically opens or closes the gate.

## Components

- **RFID Module**
- **ESP32**
- **Arduino Uno**
- **Ultrasonic Sensor**
- **NodeMCU**
- **LCD Display**
- **Servo Motor**
- **Mobile Access Request Part**
  - **Technology Stack**: Firebase

## Installation and Setup

1. **Hardware Setup**:
   - Connect the ultrasonic sensor to the ATMega328P microcontroller.
   - Interface the RFID reader with the microcontroller.
   - Connect the servo motor to the microcontroller for gate operation.
   - Set up the LCD display to show user prompts and messages.

2. **Software Setup**:
   - Install the necessary libraries for Arduino and ESP32.
   - Configure Firebase for handling mobile access requests and real-time data synchronization.
   - Program the microcontroller with the provided code for sensor interfacing, access control, and gate operation.

3. **Testing**:
   - Simulate vehicle detection using the ultrasonic sensor.
   - Test NFC access using RFID cards.
   - Validate the owner-requested access feature via the mobile app.

## Micro Controller
  ESP32 (in ESP_servo.ino):

This microcontroller is used for connecting to a Wi-Fi network and creating a web server to control a servo motor. You are utilizing the Wi-Fi capability of the ESP32 to allow clients to connect and send commands (e.g., moving the servo to the left or right). The code includes libraries like WiFi.h and ESP32Servo.h, which are specific to ESP32 development.
Arduino (e.g., Arduino Uno) (in Arduino.ino):

This microcontroller is used with an MFRC522 RFID reader to read and authenticate RFID tags/cards and control a servo motor based on the read data. The code uses the MFRC522 library for RFID communication and Servo.h for controlling a servo motor. The hardware connections specified in the code, such as RST_PIN and SS_PIN, are compatible with Arduino boards.

## Future Enhancements

- Integration with home automation systems.
- Adding facial recognition for an additional layer of security.
- Implementing AI-driven analytics for access patterns and security monitoring.


##Contact
hasithapramuditha@gmail.com | mkmalith2000@ieee.org  | darshika197@gmail.com 
