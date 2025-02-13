
# Namen Idee
- Lunar Star Survive
- Solar Starfarers
- Mission: Far Star

# Tools
- Scala
- IntelliJ Ultimate
- GitHub

# Was tut mein Programm?
Es gibt verschiedene Senarien und Events, welche passieren können:

## Spiele bei Clients Starten
 - Zeitbasiert
 - Eventtrigger

## Spiele Auswerten
- Wartet auf Antwort
- Verarbeitet, ob die Aufgabe erfolgreich war
- Sendet Einfluss auf andere Spieler

## Direkte Trigger
- z.B. Kolision -> Schlechtere Werte -> Kapitän, Polit
- z.B. Abgeschossen -> Pilot freie Bahn
- Wenn sich ein Wert updatet, alle Werte broadcasten
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

 - Ressistenz:
   - Verbessern und Reparieren durch Engineer
   - Schaden durch kollisionen vom Piloten
   - benötigt um Schaden für Leben bei Kollision berechnen
