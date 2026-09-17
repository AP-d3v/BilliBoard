# BilliBoard — Notification Demo Script (Chrome only)



## Walkthrough (by role)

Two passes at the same story: once watching the **current player's** phone, once
watching the **next player's** phone. Pick whichever fits your time — or do both.

---

# Current player perspective

**Window A = the current player** (the phone we're watching). Window B (incognito)
= a second patron who just gets in line and nudges.

### 1 · NONE → CURRENT — join an open table

- **Window A:** go to `localhost:5173/scan/2`.
- Say: *"A patron scans the QR taped to the table. No login, no app download."*
- Type a name + email → **Join the line**. Allow notifications if it asks again.
- 🔔 **"You're in line."**
- The page now says **"You are the current player."** — the table was open, so
  they're up right away.

### 2 · NONE → NEXT — a second patron gets in line

- **Window B:** go to `localhost:5173/scan/2`, join with a different name.
  (You can also do this before the demo and just flip to the window.)
- Say: *"A second patron scans the same code."*
- Their page reads **"You are next in line."**
- Window A won't refresh on its own — that's fine, the QR page is the source of
  truth. Reload it if you want to show the line.

### 3 · CURRENT gets nudged — the main event

- **Window B:** click **"Table empty? Nudge current player"**
  (or open `localhost:5173/checkin/2` and click **"Ask if the table is free"**).
- Say: *"The waiting player thinks the table looks free. Instead of hunting down
  the current player or a bartender, they nudge."*
- 🔔 **Window A: "Are you still playing?"** with an **"I'm done"** button.
- Say: *"The current player has about two minutes. This is the only notification
  in the app you can act on without walking back to the table."*

### 4 · CURRENT gives up — "I'm done"

- **Window A:** click the **"I'm done"** button on the notification → it opens
  `localhost:5173/done/2`, which confirms the table was given up.
- Say: *"They tap 'I'm done' straight from the notification. The next player is
  promoted automatically — nobody has to find a staff member."*
- 🔔 (*"You're up"* is sent to Player Two.)
- Flip to **Window B** and reload `scan/2` — it now says **"You are the current
  player."**

---

# Next player perspective

Same story, watched from the other side. **Swap the windows:** the phone you're
watching (the **next player**) goes in **Window A**; the **current player** goes
in **Window B** (incognito — their notifications don't matter here).

Reset first: `DELETE FROM reservation WHERE table_id = 2;`, clear
`billiboardSession` in Window A, open a fresh Incognito window for Window B.

### 1 · Seat a current player

- **Window B (incognito):** `localhost:5173/scan/2`, join as "Player One".
- Page says **"You are the current player."** That's all Window B is for.

### 2 · NONE → NEXT — you join the line

- **Window A:** `localhost:5173/scan/2`, join as "Player Two". Allow notifications.
- 🔔 **Window A: "You're in line."**
- The page reads **"You are next in line for this table."**
- Say: *"Player Two is next. They can step away from the table now — the app will
  tell them when to come back."*

### 3 · NEXT nudges the current player

- **Window A:** click **"Table empty? Nudge current player"**
  (or `localhost:5173/checkin/2` → **"Ask if the table is free"**).
- Say: *"Player Two thinks the table's been sitting empty. They nudge."*
- The nudge doesn't notify Player Two — it starts Player One's 2-minute clock.
  (🔔 Player One, in Window B, gets *"Are you still playing?"* — the current-player
  notification from the first pass.)

### 4 · Two minutes pass → NEXT → CURRENT

- In real use: Player One has left the bar and doesn't answer. After ~2 minutes
  the table becomes Player Two's on its own.
- **On stage, don't wait it out.** Jump to the identical result: in **Window B**,
  click **"I'm done"** on Player One's notification (or open
  `localhost:5173/done/2`). This is exactly what the timeout does — same code path.
- 🔔 **Window A: "You're up — the table is yours."**
- Reload **Window A** → **"You are the current player."**
- Say: *"Whether the current player taps 'I'm done' or the two minutes just run
  out, Player Two gets the same push and ends up with the table. No staff, no
  arguing about whose turn it is."*

### Mention, don't demo (needs a third person)

- **"You're next in line"** — fires when the line moves *under* you: you were
  third, the person ahead took the table or dropped out, now you're next.
- **"It's your turn" (2-minute check-in)** — fires when someone *further back*
  did the nudging. Player Two didn't ask for the table in that case, so they get
  their own 2-minute window to check in before it passes them by.

---

## If a notification doesn't pop within ~5 seconds

Don't wait on stage. Reload the relevant page and say *"and here's the
notification they'd get"* — the page always shows the current state. Keep moving.

---

## Do NOT

- Open Safari / Firefox on screen.
- Try to demo the full cascade (3rd, 4th player, timed-out check-ins). The
  2-minute timer is too slow to show live — cover it on the slide.
- Make the login / sign-up flow a focus. Patrons never see it.
- Open DevTools on screen (the console has OneSignal logs).

---

