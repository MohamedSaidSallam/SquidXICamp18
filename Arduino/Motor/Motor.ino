#define DEBUGMODE 1 //Comment to disable

const int LeftForward = 7; 
const int LeftBackward = 8; 
const int RightForward = 12; 
const int RightBackward = 13;
const int Enable12 = 6;
const int Enable34 = 5;

char Command[10]; //search for a better method.

int MaxX = 85;
int MaxY = 60;

void setup(){

  pinMode(Enable12, OUTPUT);
  pinMode(Enable34, OUTPUT);
  pinMode(LeftForward , OUTPUT);
  pinMode(LeftBackward , OUTPUT);
  pinMode(RightForward , OUTPUT);
  pinMode(RightBackward , OUTPUT);
  
  Serial.begin(9600);
}
 
void loop(){
  if(Serial.available() > 0){
    delay(20);
    Serial.readBytes(Command, Serial.available());
    String CommandS = String(Command);
    #if defined(DEBUGMODE)
        Serial.println(CommandS);
    #endif
    int X = CommandS.substring(0,CommandS.indexOf(",")).toInt();
    float Y = 1 - (CommandS.substring(CommandS.indexOf(",")+1).toFloat() /255);
    
    int E12 = 0;
    int E34 = 0;

    if(X < 0){
      ChangeDir(1);
      X *= -1;
    }else{
      ChangeDir(0);
    }
    
    if(Y == 0){
      E12 = X;
      E34 = X;
    }else if(Y > 0){
      E12 = X;
      E34 =(int) X * Y;
    }else{
      E12 = (int) X * Y * -1;
      E34 = X;
    }
    analogWrite(Enable12, E12);
    analogWrite(Enable34, E34);
    #if defined(DEBUGMODE)
        Serial.println("X: "+ String(X) + " Y: " + String(Y));
        Serial.println("R: "+ String(E12) + "," + String(E34));
    #endif
    for(int i = 0; i < 10; i++){
      Command[i] = 0;
    }
  } 
}
void ChangeDir(int Dir){
    //0 --> Forward, 1 --> Backward
    digitalWrite(LeftForward, !Dir);
    digitalWrite(LeftBackward, Dir);
    digitalWrite(RightForward, !Dir);
    digitalWrite(RightBackward, Dir);
}
