import time
import sys

while True:



    print ("---------- Neuer Alarm-----------------")
        
    Stichwort = input("Stichwort: ")
    Ort = input("Ort: ")
    Ortsteil = input("Ortsteil: ")
    Straße = input("Straße: ")
    Bemerkung = input("Alarmtext: ")
    valid = False
    while valid == False:
        valid = True
        Zug = input("Zug (L, R, B): ")
        if Zug == "L":
            Fahrzeuge=["BREU-3-42", "BREU-4-19", "BREU-1-22"]
        else:
            if Zug == "R":
                Fahrzeuge=["BREU-1-64", "BREU-2-48", "BREU-2-19"]
            else:
                if Zug == "B":
                    Fahrzeuge=["BREU-1-64", "BREU-2-48", "BREU-2-19", "BREU-3-42", "BREU-4-19", "BREU-1-22"]
                else:
                    print ("Ungültiger Zug\n")
                    valid = False

    fobj = open("Alarm.txt", "w")
    fobj.write("Alarmdruck 1160047686 / Technische Hilfeleistung\n")
    fobj.write("Einsatzanlass\n")
    fobj.write("Meldebild 091 - Feuerwehreinsatz\n")
    fobj.write("Bemerkung " + Bemerkung +"\n")
    fobj.write("Stichwort " + Stichwort + "\n")
    fobj.write("Meldender - / -\n")
    fobj.write("Einsatzort\n")
    fobj.write("Ort " + Ort + "\n")
    fobj.write("Ortsteil " + Ortsteil + "\n")
    fobj.write("Straße " + Straße + "\n")
    fobj.write("EM (Stärke/AGT) zugeteilt alarmiert Wache ab EOrt an EOrt ab ZOrt an ZOrt ab Ende\n")
    for act in Fahrzeuge:
        fobj.write(act + " 19:17:46 19:18:22 --:--:-- --:--:-- --:--:-- --:--:-- --:--:-- --:--:--\n")
    fobj.write("Eskalationsstufe\n")
    fobj.write("gedruckt am " + time.strftime("%d.%m.%Y %H:%M:%S", time.localtime())  + "\n")
    fobj.write("-1-\n")

    input("Drücke ENTER um die Alarmierung auszulösen")

    fobj.close()

    print ("---------- Alarmierung läuft -----------------")
    time.sleep(3)
