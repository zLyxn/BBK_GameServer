# Namen Idee
- Lunar Star Survive
- Solar Starfarers
- Mission: Far Star

# Tools
- Scala
- IntelliJ Ultimate
- GitHub

# Was macht der Server?
Der Server verarbeitet alle Informationen, welche er von den verbundenen Clients bekommt.
Zudem sendet er auch die passenden Daten an die Clients, welche diese Daten benötigen.
Zusetzlich startet dieser Events, welche das Spiel zufällig beeinflussen.

## Spiele bei Clients Starten
 - Zeitbasiert
 - Eventtrigger

## Spiele Auswerten
- Wartet auf Antwort
- Verarbeitet, ob die Aufgabe erfolgreich war
- Sendet Einfluss auf andere Spieler

## Engineer
### color Connection
- Mehrere Punkte immer zwei Pro Farbe
- Die zwei Punkte mit der gleichen Farbe müssen verbunden werden
- Die verbindungs Linien dürfen sich nicht kreuzen

## Direkte Trigger
- z.B. Kolision -> Schlechtere Werte -> Kapitän, Pilot
- z.B. Abgeschossen -> Pilot freie Bahn
- Wenn sich ein Wert updatet, diesen neu senden
- Alle Werte werden in einem Zeitintervall gesendet.
- Rest von Teamleitern

# Struktur

## Klassen
![Class](https://mermaid.ink/svg/pako:eNp1kstqxDAMRX_FeNXSmR8I3U1nUSh0IIuBko1IlETUlo0fhZDm35vmMZkkrVfW1dWR_GhlbgqUicwVeP9CUDnQGYt-nRQhB_H8fTyKE9gAxPvEhZQJe_nMFTGi22euCNawfy9Lyn_zo2PoPvnaURLiKQ2OuBKvl5ty5qhFGiBEP5d2G8Y46QKhvjMyuqp5wy9UK93XhKqYeXeJMs7ODX447wIvVfPw-KdxvoF229Bu2jm0QO4fyvq21izQOjIFMryMQw6nkjugPEiNTgMV_TsPjEyGGjVmMum3BbjPTGbc9T6IwaQN5zIJLuJBOhOrWiYlKN9H0RYQcPokN9UCfxgzx90PTPO9Fg)
- UI/Web-Server
    - Starten
    - Stoppen
    - Konsole
- Backend
    - ConnectionEngine (Verbindung)
    - GameEngine
        - Ship (Eignschaftswerte)
        - Spielteil (Werte)
- GameEngine

# Kommunikation
- Verbindung über TCP
- Immer mit Prefix: #
- Immer mit Schlagwort nach dem Hashtag, und die Werte mit Doppelpunkt trennen.
- Beispiel: Senden der Info, dass das Schiff 2 von 5 Leben hat: #health:2:5

# Aufgaben
## GameEngine
- Energie berechnen
  - Verbrauch:
    - Pilot: Verbrauch durch bewegung
    - WeaponsOfficer: Verbracuh durch schießen
    - Engineer: Verbrauch durch Upgrades und reparieren bei ausfällen durch andere Rollen
  - Gewinnung
    - Pilot: ???
    - WeaponsOfficer: Abschießen der richtigen Feinden koordiniert durch Kapitän
    - Engineer: ???

- Leben
  - Schaden durch kollisionen vom Piloten
  - Bie 0 ist das Spiel verloren
  - Regenerieren langsam

 - Resistenz:
   - Verbessern und Reparieren durch Engineer
   - Schaden durch kollisionen vom Piloten
   - benötigt um Schaden für Leben bei Kollision berechnen
  - GameLoop (siehe unten)

## GameEvent trait
- Event Atribute:
  - trigger: Was passiert (Unit)
  - solve: Kann getriggert werden, wenn Event abgeschlossen ist (Unit)
  - start time: Wann beginnt das Event (int)
  - length: Wie lange bleibt das Event aktiev (option [int])
### Shield down Event
- Keine Resistenz durch Schilde -> mehr Schaden
- 20 Sekunden
- Schild wir deaktiviert und kann in der Zeit nicht aktiviert werden
- Kapitän bekommt nur eine Info zum Beginn des Events
- Das Ende des Events wird nicht gesagt, es passiert nur im Hintergrund.
- Nach Ende des Events kann das Schild wieder aktiviert werden, es kommt aber keine Info
### Drive broken Event
- Pilot kann nicht mehr fliegen
- Ingenieur muss fixen
- Wenn Ingenieur gelöst hat, wird NICHTS gesendet / angezeigt, der Ingenieur muss im Reallife Kommunizieren, dass das Event vorbei ist.
### Weapons broken Event
- Waffen Offizier kann nicht mehr schießen
- Pilot muss etwas einsammeln
### Angriff Event
- Pilot & Waffen Offizier sehen ein anderes Raumschiff, welches uns angreift
- Dabei entsteht Schaden für das Raumschiff
- Pilot muss Angriffen ausweichen
- Waffen offizier muss Feind abschießen

# GameLoop
## Allgemeiner Verbrauch
### Energie
- Energie wird verbraucht durch das Fliegen (Pilot) und schießen (Waffen Offizier)
- Energie wird gewonnen durch das abschießen (Waffeb Offizier) der richtigen Farben
### Luft
- Die Luft wird immer verbraucht und kann schneller verloren gehen, wenn Probleme auftreten
- Kann durch treffen (Pilot) der richtigen Farben aufgefüllt werden.

## Zufällige Werte
### Farbe
- Für Pilots Spiel, die gute Farbe
- Anzeigen in Kaptain
- ?? Enginieur Spiel evt.
### Meteor Amount
- Schwierigkeit für Pilot (Anzahl)
- Bei wenig Leben wird einfacher

## Events
Die perfekte Formel für die Zeit in Sekunden zwischen der Events

g(x) = (M-o)/(1+e^(k*(x-T)))+o

- M = 180
- k = 0.7
- T = 5
- o = 30

# Netzwerkbefehle
## Server
**Sendet (an alle):**
- `#game:start`  
  Das Spiel startet JETZT  
- `#game:over:<Begründung: String>`  
  Das Spiel wurde verloren  
- `#game:won`  
  Das Spiel wurde gewonnen  

**Empfängt (von vielen):**
- `#role:<Rolle>`  
  Damit legt man die Rolle des Clients fest, jeder muss diesen Befehl nach der Verbindung triggern  

---

## Kapitän
**Sendet:**
- `#role:Captain`

**Empfängt:**
- `#health:<current>:<max>`  
  Aktuelle und maximale Leben  
- `#shield:<boolean>`  
  Schildstatus (true/false)  
- `#repaircolor:<color/none>`  
  Farbe für Reparatur oder `none`  
- `#energy:<current>:<max>`  
  Aktuelle Energie  
- `#event:<Event>`  
  Aktuelles Event  
- `#drive:<current>:<max>`  
  Geschwindigkeit des Schiffs  
- `#coreAir:<current>:<max>`  
  Restliche Luft im Schiff  
- `#repairPoints:<Value>`  
  Verfügbare Reparaturpunkte  
- `#weapons:<Boolean>`  
  Waffenstatus  
- `#pushAirSupply:<Boolean>`  
  Luftversorgung aktiv/kaputt  
- `#drive:<Boolean>`  
  Pilot kann fliegen oder nicht  

---

## Ingenieur
**Sendet:**
- `#role:Engineer`
- `#shield:<boolean>`
- `#airSupply:<boolean>`
- `#drive:<int (in %)>`
- `#weapons:<boolean>`
- `#minigame:<start/win>`  
  - `start`: Minigame gestartet (−10 Energie)  
  - `win`: Minigame gewonnen (+1 Reparaturpunkt)  
- `#repair:<shield/weapons/airsupply/drive>`  
  25% Chance bei Schaden, dass System kaputt geht  

**Empfängt:**  nichts

---

## Pilot
**Sendet:**
- `#role:Pilot`
- `#hitmeteor:<Color>`  
  Meteor getroffen mit Farbe `<Color>`  

**Empfängt:**
- `#drive:<current>:<max>`  
  Geschwindigkeit vom Ingenieur  
- `#meteorAmount:<number>`  
  Anzahl abgeschossener Meteoren (Score)  
- `#newEnemy`  
  Neuer Gegner erscheint  
- `#drive:<Int (in %)>`  
 Geschwindigkeit

## Waffenoffizier
**Sendet:**
- `#role:WeaponsOfficer`
- `#shoot:<Type>:<optional[color]>`  
  Type: meteor / Lootbox (good / bad – 50/50) / Spaceship (good / bad)

**Empfängt:**
- `#ammo:<Anzahl>`  
  Anzahl der verfügbaren Munition. Wenn Munition = 0, kann nicht mehr geschossen werden.
- `#newEnemy`  
  Erzeugt einen neuen Gegner, den man abschießen muss
- `#weapons:<Boolean>`  
  true: Waffen funktionieren, false: Waffen sind kaputt
