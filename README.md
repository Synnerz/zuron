# zuron
zuron is a project that aims to allow users to use javascript, python and lua in modern versions of minecraft

# Motivation
I ([DocilElm](https://github.com/DocilElm)) am the lead maintainer of a fork for [Chattriggers Fabric](https://github.com/Synnerz/ctjs), unluckily i do not have enough time to keep it up to date, this project aims to allow for a simpler api which does not require any Minecraft code so it can be easily ported to newer versions.

# Commands
Currently, there only exists a `/zr load` command to reload all the engines at once

# Engines
* [mozilla/rhino v1.9.1](https://github.com/mozilla/rhino)
* [jython v2.7.4](https://github.com/jython/jython)
* [luaj v3.0.1](https://github.com/luaj/luaj)

NOTE: these may change later on.

# Is It Useful?
The main plan was to have support for mixins so our users could just build their own events, sadly that is a bit more difficult than i expected so it currently does not and _seems_ kind of useless, one option is to use another mod's event system or something similar.

# Loading Scripts
Loading your scripts is simple:

Head over to your `~/.minecraft` folder then into `config` and you should see a folder called `zuron` inside of this there should be 3 different folders named `js`, `py` and `lua`

Open the folder of which your script's language is written (or one you want to use) on then create a new folder with your script name and the initial file.

Initial file name is enforced by zuron due to it loading one single file instead of the entire directory, all you need to do is:
* `JS` -> `index.js`
* `PY` -> `main.py`
* `Lua` -> `main.lua`

yes sadly there is not a command currently to open the scripts folder

# Snippets
## JavaScript
NOTE: modern rhino engine only supports CommonJS imports
```js
// similar to CTJS we have support for Java.type
const System = Java.type("java.lang.System")

// printing a message into the user's console (latest log)
System.out.println("printing to console through JS")

// this is our "event system" (forgot to make it load automatically)
const event = Java.type("com.github.synnerz.zuron.internal.Register")

// subscribe/register to an event
event.register("nameOfEventHere", (arg1, arg2) => {
    // the code inside of here will run whenever the event gets triggered
})

// triggering an event
event.trigger("nameOfEventHere", 1, 2)
```

## Lua
```lua
local System = Java.type("java.lang.System")

System.out:println("printing to console through Lua")

local event = Java.type("com.github.synnerz.zuron.internal.Register")

event:register("nameOfEventHere", function(arg1, arg2)
    -- the code inside of here will run whenever the event gets triggered
    end
)

event:trigger("nameOfEventHere", {1, 2})
```

## Python
```py
from java.lang import System

System.out.println("printing to console through Python")

from com.github.synnerz.zuron.internal import Register

def cb(arg1, arg2):
    # the code inside of here will run whenever the event gets triggered

Register.register("nameOfEventHere", cb)

Register.trigger("nameOfEventHere", 1, 2)
```

# Credits
Special thanks to [Chattriggers Fabric](https://github.com/ChatTriggers/ctjs) for being the main motivation behind this project.