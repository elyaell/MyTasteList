#!/usr/bin/env python
# -*- coding: utf-8 -*-

import matplotlib.pyplot as plt

titles = []

def tri(lines):
    lines.sort()
    for l in lines:
        print l,


res = []

def delete_data_useless(textRef, textChange):
    dejaVu = []
    fr = open(textRef, 'r')
    fc = open(textChange, 'r')
    fres = open('stat_res.txt', 'w')
    linesRes = []
    linesR = fr.readlines()
    linesC = fc.readlines()
    for lr in linesR:
        #On garde juste le titre du film pour le premier fichier
        split = lr.split(" ")
        tmp = ""
        for i in range(len(split) - 1):
            tmp = tmp +" "+ split[i]
        tmp = tmp[1:]
        #print "tmp 1 " + tmp
        #On fait pareil pour le second
        for lc in linesC:
            #print lc
            split2 = lc.split(" ")
            tmp2 = ""
            #print split2[len(split2) - 1]
            for i in range(len(split2) - 1):
                tmp2 = tmp2 +" "+ split2[i]
            tmp2 = tmp2[1:]
            #print "tmp 2 " + tmp2
            #On compare les 2 titres de films
            if tmp == tmp2 and tmp2 not in dejaVu:
                dejaVu.append(tmp2)
                titles.append(tmp2)
                #fres.write(lr+" "+split2[len(split2) - 1])
                #print lc+" "+split[len(split) - 1]
                tmp3 = split[len(split)-1]
                linesRes.append(lc[0:len(lc)-1]+" "+tmp3[0:len(tmp3)-1])
                #print lc+" "+lr
                #print lc[0:len(lc)-1]+" "+tmp3[0:len(tmp3)-1]
                fres.write(lc[0:len(lc)-1]+" "+tmp3[0:len(tmp3)-1]+"\n")
    print linesRes
    fc.close()
    fr.close()
    fres.close()
    res = linesRes

delete_data_useless("cosinus.txt", "Techbychev.txt")
resCos = []
resTcheby = []


print res


for data in res:
    tmp = data.split(" ")
    resCos.append(data[len(data) - 1])
    resTcheby.append(data[len(data) - 1])



x = [resCos]
n, bins, patches = plt.hist(x, 50, normed=1, facecolor='b', alpha=0.5)

plt.xlabel('Mise')
plt.ylabel(u'Probabilité')
plt.axis([0, 150, 0, 0.02])
plt.grid(True)
plt.show()
#tri(resCos)
#tri(resTcheby)

