#include <Arduino.h>
//#define DEBUGMODE 1 //Comment to disable

const int Input1 = 7; 
const int Input2 = 8; 
const int Input3 = 11; 
const int Input4 = 12;
const int Enable12 = 5;
const int Enable34 = 6;

char Command[11];

//int Dir; //Delete me 

void setup(){

  pinMode(Enable12, OUTPUT);
  pinMode(Enable34, OUTPUT);
  pinMode(Input1 , OUTPUT);
  pinMode(Input2 , OUTPUT);
  pinMode(Input3 , OUTPUT);
  pinMode(Input4 , OUTPUT);
  
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
    float Y = CommandS.substring(CommandS.indexOf(",")+1).toFloat()/255;
    
    int E12 = 0;
    int E34 = 0;

    if(X < 0){
      ChangeDir(1);
      //Dir = 1;
      X *= -1;
    }else{
      ChangeDir(0);
      //Dir = 0;
    }
    
    if(Y < 0){
      Y = 1 + Y;
      E12 = X;
      E34 =(int) X * Y;
    }else{
      Y = 1 - Y;
      E12 = (int) X * Y;
      E34 = X;
    }
    analogWrite(Enable12, E12);
    analogWrite(Enable34, E34);

    #if defined(DEBUGMODE)
        Serial.println("X: "+ String(X) + " Y: " + String(Y));
        Serial.println("R: "+ String(E12) + "," + String(E34));
        //Serial.println("Dir:" + String(Dir));
    #endif

    for(int i = 0; i < 10; i++){
      Command[i] = 0;
    }
  } 
}
void ChangeDir(int Dir){
    //0 --> Forward, 1 --> Backward
    digitalWrite(Input1, !Dir);
    digitalWrite(Input2, Dir);
    digitalWrite(Input3, !Dir);
    digitalWrite(Input4, Dir);
}
