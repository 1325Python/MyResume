const int led = 8;      // LED connected to pin 8
const int pirPin = 2;   // PIR sensor OUT connected to pin 2

void setup() {
  pinMode(led, OUTPUT);    // set LED as output
  pinMode(pirPin, INPUT);  // set PIR as input
  digitalWrite(led, LOW);  // start LED OFF
}

void loop() {
  // LED directly follows PIR output: HIGH = motion, LOW = no motion
  digitalWrite(led, digitalRead(pirPin));
}
