# Takoyaki 2.1.1
## 2015-09-18

### New features
- Now you can set `Mailer.HOOK_URL` to enable ArticleDoctor.



# Takoyaki 2.1
## 2015-08-13

### New features
- Now you can modify or transmit logs by new classes
- Now it shows the upload date of the article
- Real-time article status checker for mail contents

### Bug fixes
- Fixed a encoding issue of plugin loading
- Applied Korean locale for timestamps

### Tweaks/other
- Added a new target to the default config
- Don't show the version of the plugin if missing
- Modified some items of `Violation.Level`
- Removed default username of the mail account



# Takoyaki 2.0.2
## 2015-05-21

### Bug fixes
- Fixed wrong encoding options of the mail



# Takoyaki 2.0.1
## 2015-05-09

### New features
- Added an option to exclude some plugins
- Create the properties.json file if not exists
- Show the version of the plugin on enabling messages
- Show recipients of the mail on console alert

### Bug fixes
- Crash when data included `percent symbols (%)`
- CSS statements of the mail are showing in some web browsers

### Tweaks/other
- Fixed the typo of `onDestroy`
- Renamed `Loggable.printf(...)` to `Loggable.log(...)`
- A better format for timestamps of log



# Takoyaki 2.0
## 2015-04-27

- First release!