import serial 
import time
import pyautogui  

ArduinoSerial=serial.Serial('COM5',9600)
time.sleep(2)

while 1:
    incoming = str(ArduinoSerial.readline())
    print(incoming)
    
    if 'PLAY/PAUSE' in incoming:  #permet de démarrer ou arrêter une musique/vidéo
        pyautogui.press('space') #permet de simuler la touche espace
        
    if 'VOL++' in incoming:  #permet d'augmenter le volume
        pyautogui.press('volumeup')
        
    if 'VOL--' in incoming:  #permet de diminuer le volume
        pyautogui.press('volumedown')
         
    if 'FORWARD' in incoming:  #permet d'avancer la vidéo/musique
        pyautogui.keyDown('shift')            
        pyautogui.press('right')    
        pyautogui.keyUp('shift')