const int index=5;
const int majeur=4;
const int annulaire=3;
const int auriculaire=2;
int val1=0;
int val2=0;
int val3=0;
int val4=0;

void setup() {
  Serial.begin(9600);
  pinMode(index,INPUT);
  pinMode(majeur,INPUT);
  pinMode(annulaire,INPUT);
  pinMode(auriculaire,INPUT);
}

void loop() {
  val1=digitalRead(index);
  val2=digitalRead(majeur);
  val3=digitalRead(annulaire);
  val4=digitalRead(auriculaire);
  
  if (val1==LOW){
    Serial.println("PLAY/PAUSE");
  }
  if (val2==LOW){
    Serial.println("VOL++"); 
  }
  if (val3==LOW){
    Serial.println("VOL--");
  }
  if (val4==LOW){
    Serial.println("FORWARD");
  }
  
  delay(150);
}
