var exec = require('cordova/exec');

var BeaconPlugin = {
    startScan: function(successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'BeaconPlugin', 'startScan', []);
    }
};

module.exports = BeaconPlugin;