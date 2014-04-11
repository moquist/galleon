galleon [![Build Status](http://img.shields.io/travis/vlacs/galleon/dev.svg)](https://travis-ci.org/vlacs/galleon)
=======
<a title="By Myriam Thyes (Own work) [CC-BY-SA-3.0 (http://creativecommons.org/licenses/by-sa/3.0) or GFDL (http://www.gnu.org/copyleft/fdl.html)], via Wikimedia Commons" href="http://commons.wikimedia.org/wiki/File%3AVenedig-galeone-16jh.jpg"><img width="128" alt="Venedig-galeone-16jh" src="http://upload.wikimedia.org/wikipedia/commons/thumb/2/2e/Venedig-galeone-16jh.jpg/128px-Venedig-galeone-16jh.jpg"/></a>

Galleon will be VLACS' competencies-centric education tracking system.

Dev/Test Workflow
=======

1. Get immutant if you don't have it yet.
    1. Add ```[lein-immutant "1.2.0"]``` to your ```~/.lein/profiles.clj``` file.
    1. Run ```lein immutant install```.
1. Deploy galleon to immutant:
    1. ```cd galleon```
    1. ```lein immutant deploy```
1. Start up immutant in another terminal with ```lein immutant run```. Watch for the nREPL connection port for your deployment of galleon, though immutant sets .nrepl-port in your project root dir correctly so this isn't essential.
    1. Connect from the CLI: ```lein repl :connect``` (automatically uses .nrepl-port)
    1. Connect from emacs: ```M-x cider``` and accept the defaults.
1. Test your nREPL to be sure you're in immutant: ```(util/in-immutant?)```
1. ```immutant/init.clj``` initializes the project, so it's already running. But you can ```(reset)``` it if you like:
    1. ```(reset)``` will reload your code without deleting the Datomic database.
    1. ```(reset-and-delete-db! :delete-db)``` will reload your code AND DELETE THE DATOMIC DATABASE.
1. See REPL-code examples in ```(comment ...)``` in ```dev/user.clj```.
