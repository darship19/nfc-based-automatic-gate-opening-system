#include <SPI.h>
#include <MFRC522.h>
#include <Servo.h>

#define RST_PIN         9          // Configurable, see typical pin layout above
#define SS_PIN          10         // Configurable, see typical pin layout above
#define SERVO_PIN       7          // Pin connected to the servo signal line

MFRC522 mfrc522(SS_PIN, RST_PIN);  // Create MFRC522 instance
Servo myServo;                      // Create a servo object

void setup() {
    Serial.begin(9600);         // Initialize serial communications with the PC
    SPI.begin();                // Init SPI bus
    mfrc522.PCD_Init();         // Init MFRC522
}

void loop() {
    // Look for new cards
    if (!mfrc522.PICC_IsNewCardPresent()) {
        return;
    }

    // Select one of the cards
    if (!mfrc522.PICC_ReadCardSerial()) {
        return;
    }

    // Authenticate using key A
    MFRC522::MIFARE_Key key;
    for (byte i = 0; i < 6; i++) key.keyByte[i] = 0xFF;  // default key is 0xFF

    byte block = 4; // Block to read from
    byte buffer[18];
    byte size = sizeof(buffer);

    // Authenticate the block
    MFRC522::StatusCode status = mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, block, &key, &(mfrc522.uid));
    if (status != MFRC522::STATUS_OK) {
        Serial.print(F("No"));
        loop();
    }

    // Read data from the block
    status = mfrc522.MIFARE_Read(block, buffer, &size);
    if (status != MFRC522::STATUS_OK) {
        Serial.print(F("No"));
        loop();
    }

    // Check the read data
    buffer[16] = '\0'; // Null-terminate the string
    String employeeName = (char*)buffer;

    if (employeeName.equals("Hasitha        ")) {
        Serial.println(F("Yes"));
        delay(2000);
        loop();
    } else {
        Serial.println(F("No"));
        delay(2000);
        loop();
    }

    // Halt PICC
    mfrc522.PICC_HaltA();
    // Stop encryption on PCD
    mfrc522.PCD_StopCrypto1();
}