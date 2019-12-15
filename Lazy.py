import pyautogui
import time

f = open("lazy.txt", "r")
contents = f.read();
# print(contents)
time.sleep(5)
count = -1;
for line in contents.splitlines():
    count += 1
    print(line)
    if(count == 0):
        pyautogui.typewrite(line)
        pyautogui.keyDown('right')
        pyautogui.keyUp('right')
        pyautogui.keyDown('right')
        pyautogui.keyUp('right')
    if(count == 1):
        pyautogui.typewrite(line)
        pyautogui.keyDown('left')
        pyautogui.keyUp('left')
    if(count == 2):
        pyautogui.typewrite(line)
        pyautogui.keyDown('right')
        pyautogui.keyUp('right')
        pyautogui.keyDown('right')
        pyautogui.keyUp('right')
    if(count == 3):
        pyautogui.typewrite(line)
        pyautogui.keyDown('right')
        pyautogui.keyUp('right')
    if(count == 4):
        pyautogui.typewrite(line)
        pyautogui.keyDown('right')
        pyautogui.keyUp('right')
    if(count == 5):
        pyautogui.keyDown('down')
        pyautogui.keyUp('down')
        pyautogui.keyDown('left')
        pyautogui.keyUp('left')
        pyautogui.keyDown('left')
        pyautogui.keyUp('left')
        pyautogui.keyDown('left')
        pyautogui.keyUp('left')
        pyautogui.keyDown('left')
        pyautogui.keyUp('left')
        count = -1
