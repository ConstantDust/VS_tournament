package org.valkyrienskies.tournament

import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema

object tournamentConfig {
    @JvmField
    val CLIENT = Client()

    @JvmField
    val SERVER = Server()

    class Client

    class Server {

        @JsonSchema(description = "The Force a spinner will output")
        var SpinnerSpeed = 5000.0

        @JsonSchema(description = "The Force a balloon will output")
        var BalloonPower = 3000.0

        @JsonSchema(description = "The Force a balloon will output")
        var GyroPower = 100000.0

        @JsonSchema(description = "The Force a spinner will output")
        var ThrusterSpeed = 10000.0

        @JsonSchema(description = "Whether the ship helm assembles diagonally connected blocks or not")
        var diagonals = true

        @JsonSchema(description = "Weight of ballast when lowest redstone power")
        var ballastWeight = 10000.0

        @JsonSchema(description = "Weight of ballast when highest redstone power")
        var ballastNoWeight = 10.0

        @JsonSchema(description = "The Force the Pulse Gun applies")
        var pulseGunForce = 500.0
    }
}
