#include <DigiCDC.h> //biblioteca para comunicação com o celular
#include <TinyWireM.h> //biblioteca adicional para comunicação USB

#define MLX90614_I2CADDR 0x5A

// RAM
#define MLX90614_TOBJ1 0x07

//funcaoes
double readTemp();


//configura pinos para comunicação I2C
#define TINY_SDA   =     0;               
#define TINY_SCL   =     2;   

void setup() {
  //inicialização dos parâmetros
  SerialUSB.begin(); 
  TinyWireM.begin();    
}

void loop() {  
//checa se o cabo USB está conectado com o celular
  if (SerialUSB.available()) {
    //cria variável 'valor' e atribui a ela o valor a ser enviado pelo celular
    char valor = SerialUSB.read();
    //se o valor for 0 será medida da temperatura do objeto
    if(valor == '0'){ 
      //variável que guarda a temperatura do objeto
      //envia valor medido para o celular via USB
      SerialUSB.println(readTemp()); 
    }
  }
  //mantem a comunicação ativa 
  SerialUSB.refresh();

}

double readTemp() {
  uint16_t ret;
  TinyWireM.beginTransmission(MLX90614_I2CADDR); // start transmission to device
  TinyWireM.write(MLX90614_TOBJ1); // sends register address to read from
  TinyWireM.endTransmission(false); // end transmission

  TinyWireM.requestFrom(MLX90614_I2CADDR, (uint8_t)3);// send data n-bytes read
  ret = TinyWireM.read(); // receive DATA
  ret |= TinyWireM.read() << 8; // receive DATA
  TinyWireM.read();
  
  return ((ret*0.02)-273.15);
}
