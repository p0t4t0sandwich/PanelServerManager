package ca.sperrer.p0t4t0sandwich.panelservermanager.manager.cubecodersamp;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import ca.sperrer.p0t4t0sandwich.panelservermanager.manager.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class AMPServer extends Server {
    public final String serverName;
    private final String instanceName;
    private final String instanceId;
    private final AMPAPIHandler API;
    private HashMap<?, ?> loginResult;

    /**
     * Constructor for the Instance class.
     * @param serverName: The name that the server is referred to
     * @param instanceName: The InstanceName of the AMP instance
     * @param instanceId: The InstanceID of the AMP instance
     * @param API: The AMPAPIHandler object for the instance
     */
    public AMPServer(String serverName, String instanceName, String instanceId, AMPAPIHandler API) {
        super(serverName);
        this.serverName = serverName;
        this.instanceName = instanceName;
        this.instanceId = instanceId;
        this.API = API;
        this.loginResult = API.Login();
    }

    @Override
    public boolean isOnline() {
        return (loginResult != null && (boolean) loginResult.get("success"));
    }

    @Override
    public boolean reLog() {
        this.loginResult = API.Login();
        return (loginResult != null && (boolean) loginResult.get("success"));
    }

    /**
     * Acts as a wrapper for the Instance object to abstract the API.
     * @param method: The method to run
     * @return The result of the method
     */
    public HashMap<?, ?> runMethod(Function<String[], HashMap<?, ?>> method) {
        try {
            return method.apply(new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * Abstraction for API.Core.Start
     */
    @Override
    public void startServer() {
        runMethod((args) -> API.Core_Start());
    }

    /**
     * Abstraction for API.Core.Stop
     */
    @Override
    public void stopServer() {
        runMethod((args) -> API.Core_Stop());
    }

    /**
     * Abstraction for API.Core.Restart
     */
    @Override
    public void restartServer() {
        runMethod((args) -> API.Core_Restart());
    }

    /**
     * Abstraction for API.Core.Kill
     */
    @Override
    public void killServer() {
        runMethod((args) -> API.Core_Kill());
    }

    /**
     * Abstraction for API.Core.SendConsoleMessage
     * @param message: The message/command to send
     */
    @Override
    public void sendCommand(String message) {
        runMethod((args) -> API.Core_SendConsoleMessage(message));
    }

    /**
     * A parser for this.getStatus()
     * @param status: The status object from the API
     * @return A parsed status object
     */
    public HashMap<String, Object> parseStatus(HashMap<?, ?> status) {
        if (status == null) {
            return null;
        }

        HashMap<String, Object> newStatus = new HashMap<>();

        // Check if Metrics is present and parse it
        if (status.containsKey("Metrics")) {
            HashMap<?, ?> Metrics = (HashMap<?, ?>) status.get("Metrics");
            double CPU = (double) ((HashMap<?, ?>) Metrics.get("CPU Usage")).get("Percent");
            newStatus.put("CPU", CPU);

            // Check if the Metrics contains Memory Usage and add it to the newStatus object
            if (Metrics.containsKey("Memory Usage")) {
                HashMap<?, ?> Memory = (HashMap<?, ?>) Metrics.get("Memory Usage");
                int MemoryValue = (int) Math.round((double) Memory.get("RawValue"));
                int MemoryMax = (int) Math.round((double) Memory.get("MaxValue"));
                newStatus.put("MemoryValue", MemoryValue);
                newStatus.put("MemoryMax", MemoryMax);
            }

            // Check if the Metrics contains Active Users and add it to the newStatus object
            if (Metrics.containsKey("Active Users")) {
                HashMap<?, ?> Players = (HashMap<?, ?>) Metrics.get("Active Users");
                int PlayersValue = (int) Math.round((double) Players.get("RawValue"));
                int PlayersMax = (int) Math.round((double) Players.get("MaxValue"));
                newStatus.put("PlayersValue", PlayersValue);
                newStatus.put("PlayersMax", PlayersMax);
            }

            // Check if the Metrics contains TPS and add it to the newStatus object
            if (Metrics.containsKey("TPS")) {
                HashMap<?, ?> TPS = (HashMap<?, ?>) Metrics.get("TPS");
                double TPSValue = (double) TPS.get("RawValue");
                newStatus.put("TPSValue", TPSValue);
            }
        }

        // Convert state to string
        if (status.containsKey("State")) {
            int state = (int) Math.round((double) status.get("State"));
            switch (state) {
                case -1:
                    newStatus.put("State", "Undefined");
                    break;
                case 0:
                    newStatus.put("State", "Stopped");
                    break;
                case 5:
                    newStatus.put("State", "PreStart");
                    break;
                case 7:
                    newStatus.put("State", "Configuring");
                    break;
                case 10:
                    newStatus.put("State", "Starting");
                    break;
                case 20:
                    newStatus.put("State", "Ready");
                    break;
                case 30:
                    newStatus.put("State", "Restarting");
                    break;
                case 40:
                    newStatus.put("State", "Stopping");
                    break;
                case 45:
                    newStatus.put("State", "PreparingForSleep");
                    break;
                case 50:
                    newStatus.put("State", "Sleeping");
                    break;
                case 60:
                    newStatus.put("State", "Waiting");
                    break;
                case 70:
                    newStatus.put("State", "Installing");
                    break;
                case 75:
                    newStatus.put("State", "Updating");
                    break;
                case 80:
                    newStatus.put("State", "AwaitingUserInput");
                    break;
                case 100:
                    newStatus.put("State", "Failed");
                    break;
                case 200:
                    newStatus.put("State", "Suspended");
                    break;
                case 250:
                    newStatus.put("State", "Maintenance");
                    break;
                case 999:
                    newStatus.put("State", "Indeterminate");
                    break;
            }
        }
        return newStatus;
    }

    /**
     * Abstraction for API.Core.GetStatus
     *
     * @return The status object from the API
     */
    @Override
    public HashMap<String, Object> getStatus() {
        HashMap<?, ?> status = runMethod((args) -> API.Core_GetStatus());
        return parseStatus(status);
    }

    /**
     * A parser for this.getPlayerList()
     * @param playerList: The player list object from the API
     * @return A parsed player list object
     */
    public List<String> parsePlayerList(HashMap<?, ?> playerList) {
        if (playerList == null) {
            return null;
        }
        List<String> players = new ArrayList<>();
        for (HashMap.Entry<?, ?> entry : playerList.entrySet()) {
            players.add((String) entry.getValue());
        }
        return players;
    }

    /**
     * Abstraction for API.Core.GetUserList
     * @return The player list object from the API
     */
    @Override
    public List<String> getPlayerList() {
        HashMap<?, ?> playerList = runMethod((args) -> (HashMap<?, ?>) API.Core_GetUserList().get("result"));
        return parsePlayerList(playerList);
    }

    /**
     * Abstraction for API.Core.Sleep
     */
    public void sleepServer() {
        runMethod((args) -> API.Core_Sleep());
    }

    /**
     * Abstraction for API.LocalFileBackupPlugin.TakeBackup
     * @param backupTitle: The title of the backup
     * @param backupDescription: The description of the backup
     * @param isSticky: Whether the backup is sticky or not
     */
    public void backupServer(String backupTitle, String backupDescription, boolean isSticky) {
        runMethod((args) -> API.LocalFileBackupPlugin_TakeBackup(backupTitle, backupDescription, isSticky));
    }
}