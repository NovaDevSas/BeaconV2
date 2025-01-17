#import <Cordova/CDV.h>
#import <CoreLocation/CoreLocation.h>

@interface BeaconPlugin : CDVPlugin <CLLocationManagerDelegate>
@property (nonatomic, strong) CLLocationManager *locationManager;
@end

@implementation BeaconPlugin

- (void)startScan:(CDVInvokedUrlCommand*)command {
    self.locationManager = [[CLLocationManager alloc] init];
    self.locationManager.delegate = self;
    [self.locationManager requestWhenInUseAuthorization];
    NSUUID *uuid = [[NSUUID alloc] initWithUUIDString:@"YOUR_UUID_HERE"];
    CLBeaconRegion *region = [[CLBeaconRegion alloc] initWithProximityUUID:uuid identifier:@"myBeaconRegion"];
    [self.locationManager startRangingBeaconsInRegion:region];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Beacon scanning started"];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)locationManager:(CLLocationManager *)manager didRangeBeacons:(NSArray<CLBeacon *> *)beacons inRegion:(CLBeaconRegion *)region {
    for (CLBeacon *beacon in beacons) {
        NSLog(@"Beacon detected: %@", beacon);
    }
}

@end