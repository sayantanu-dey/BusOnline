import time
import RPi.GPIO as GPIO
import os
def main():

    GPIO.setmode(GPIO.BCM)
    GPIO.setup(17,GPIO.IN)
    while True:
        count=1
        value=1
        flag =1
        while count<=1000:
            value =GPIO.input(17)
            print(str(count) + " "+ str(value))
            if value==0:
                flag = 0
                break
            count=count+1
        if flag == 1:
            print("----------------------------------------blocked------------------------------------------------------")
            os.system("python3 gender-detection-keras/detect_gender.py --i gender-detection-keras/image.jpg")

