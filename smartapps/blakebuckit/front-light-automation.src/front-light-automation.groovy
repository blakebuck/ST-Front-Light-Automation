/**
 *  Front Light Automation
 *
 *  Copyright 2016 Blake Buck
 *
 */
definition(
    name: "Front Light Automation",
    namespace: "blakebuckit",
    author: "Blake Buck",
    description: "A better front light control.",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Turn on when opened:") {
		input "contact", "capability.contactSensor", required: true, title: "What?"
    }
	section("When there's motion on any of these sensors") {
		input "motion", "capability.motionSensor", required: true
	}
	section("Turn on these lights") {
		input "switches", "capability.switch", multiple: true, required: true, title: "Which?"
	}
	section("Turn light off when no motion for ") {
		input "timeOn", "number", description: "Number of minutes", required: true, title: "Minutes"
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(contact, "contact.open", turnLightOn)
	subscribe(motion,  "motion.active", turnLightOn)
}

def turnLightOn(evt){
	if ("open" == evt.value || "active" == evt.value){
		if (nightTime()){
			switches?.on()
			lightTimer()
		}		
	}
}

def lightTimer(){
	def delay = (timeOn != null && timeOn != "") ? timeOn * 60 : 600
	runIn(delay, turnLightOff)
}


def turnLightOff(){
	switches?.off()
}

def nightTime(){
	if(getSunriseAndSunset().sunrise.time < now() && getSunriseAndSunset().sunset.time > now()){
		// Daytime
		return false
	}
	else {
		return true
	}
}