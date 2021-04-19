# ALP - Albanian Programming Language
### Gjuha e pare programuese ne gjuhen **Shqipe**.

ALP eshte nje gjuhe programuese e interpretuar, dinamike dhe e orientuar ne objekte, qellimi i te cilit eshte edukimi i fillestareve ne programim. Sintaksa, thjeshtesia dhe forca e ALP jane aftesite primare. Programimin do ta mesoni ne gjuhen ametare, pa ditur gjuhen Angleze.</br>

Momentalisht eshte testuar ne sistemin operativ 'Linux' dhe per ta instaluar navigoni ne
direktoriumin `/opt/` dhe krijoni nje follder `alp`. Kopjoni te gjitha fajllat qe gjinden
ne kete repository dhe navigoni follderin `alp_interpreter`. </br>

Qe ALP te funksionoj pa probleme duhet te jete instaluar `Java`.</br>
Ne linux mund ta instaloni Java-n permes komandes `sudo apt install openjdk-14-jdk --yes`.</br>

Pas instalimit te Java-s, navigoni ne folderin </br>
    `/opt/alp/alp_interpreter/src` dhe egzekutoni komanden </br>
    `javac ALPLang.java`.
</br>

Pas kompajllimit te ALP ( qe mund te zgjas deri 60 sekonda ), egzekutoni shell skripten </br>
    `./alp --commander --repl` </br> 
dhe ALP do te egzekutohet.</br>

## alp Skripta dhe opsionet e saj
ALP eshte nje program qe eshte ne gjendje jo vetem te egzekutoje komandat, por edhe menyra e egzekutimit te programit eshte dinamike dhe mvaret nga parametrat e dhena nga perdoruesi. Keto parametra kontrollojne mbrendesine e interpretuesit te ALP dhe kombinimet e ndryshme japin rezultate te ndryshme. Ashtu pershembull nese deshirojme te egzekutojme nje ALP skripte atehere mund ta perdorim komanden `alp --file file.alp` ose `alp -f file.alp`. </br>

Nese deshirojme te shohim rezultatin e komandave drejte-per-drejte ne terminal, atehere mund ta perdorim komanden `alp -r` ose `alp --repl`. Kombinuar me opsionet `--commander --debug --verbose` jemi ne gjendje te shikojme se si egzekutohet nje komande ne prapavije dhe qfare efekti ka ne gjendjen globale te interpretuesit. Opsioni `--commander` ka komanda te caktuara qe na tregojne reprezentimin te memorjes, modulet e importuara etj. `--debug` ose `-d` tregon se si tokenizohen komandat e ALP. `--verbose` ose `-v` printon me shume mesazhe nga interpretuesi.

## ALP Gjuha Programuese dhe koceptet e programimit

Derisa te shkruhet dokumentacioni i detajuar per ALP dhe aftesite e kesaj gjuhe, mund te shikoni skriptat ne follderin alpscript.
