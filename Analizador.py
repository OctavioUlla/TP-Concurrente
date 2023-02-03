import re

file = open('log.txt')
line = file.readline()

expresion = '(T1-)(.*?)((T2-)(.*?)(T4-)(.*?)(T6-)|(T3-)(.*?)(T5-)(.*?)(T7-))(.*?)(T8-)|(T9-)(.*?)(T10-)(.*?)(T11-)(.*?)(T12-)'
sub = '\g<2>\g<5>\g<7>\g<10>\g<12>\g<14>\g<17>\g<19>\g<21>'

line = (line, 1)

while(line[1] != 0):

    line = re.subn(expresion, sub, line[0])

    print(line)

if(line[0] == ''):
    print('El Test finalizo OK')
else:
    print('El test FALLO')
