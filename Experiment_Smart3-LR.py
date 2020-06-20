import random
import subprocess
import os
import math
import csv


# Old Settings:
#distances = [250, 500, 750, 1000, 1250];
#nDefenders = [20, 40, 60, 80, 100, 120, 140, 160];
#difficulties = ["easy","medium","hard"];
# New Settings:
#distances = [5500, 5000, 4500, 4000, 3500, 3000, 2500, 2000, 1500];
distances = [1500]#[1500, 2000, 2500, 3000, 3500];
nDefenders = [20, 40, 60, 80, 100, 120, 140];
difficulties = ["easy","medium","hard"];
nRepetitions = 30;
baseFolder = "results-smart3-lr/";
result=list()

for distance in distances:
    for n in nDefenders:
        for difficulty in difficulties:
            if (not os.path.exists(baseFolder + str(distance) + "-" + str(n) + "-" + difficulty)):
                os.makedirs(baseFolder + str(distance) + "-" + str(n) + "-" + difficulty)
                
            for counter in range(nRepetitions):


                if (os.path.exists(baseFolder + str(distance) + "-" + str(n) + "-" + difficulty+"/"+str(counter)+".txt")):
                    print(baseFolder + str(distance) + "-" + str(n) + "-" + difficulty+"/"+str(counter)+".txt" + " already exists!");
                    print("Skipping");
                    continue;
                    

                
                valid = False

                while (not valid):
                    cordY=random.randint(20,1001)

                    a=1
                    b=-2*550
                    c=550**2 - distance**2+(cordY-500)**2
                    d = (b**2) - (4*a*c)

                    if (d > 0):
                        valid = True

                # find two solutions
                #sol1 = -b-math.sqrt(d)/(2*a)
                #print("cordY: " + str(cordY) + " distance: " + str(distance) + " a: " + str(a) + " b: " + str(b) + " c: " + str(c) + " d: " + str(d))
                sol2 = -b+math.sqrt(d)/(2*a)
        
                cordX=int(sol2)


                baseExec = "java -jar MyCode.jar {} {} {} {} {} {}".format(cordX,cordY,counter,difficulty,n,baseFolder + str(distance) + "-" + str(n) + "-" + difficulty + "/")
                p = subprocess.Popen(baseExec, shell=True,universal_newlines=True)
                p.wait()
                os.makedirs(baseFolder + str(distance) + "-" + str(n) + "-" + difficulty + "/AttackingAndUpdatingTime-" + str(counter) + ".txt")
                

   
