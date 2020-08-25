/**
 * Copyright (C) 2018 Jason C.H
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

import 'dart:io';

import 'package:flutter/services.dart';

const _channel = const MethodChannel('flutter_vpn');
const _eventChannel = const EventChannel('flutter_vpn_states');

/// The generic VPN state for all platforms.
enum FlutterVpnState {
  disconnected,
  connecting,
  connected,
  disconnecting,
  genericError
}

/// The error state from `VpnStateService`.
/// Only available for Android device.
enum CharonErrorState {
  NO_ERROR,
  AUTH_FAILED,
  PEER_AUTH_FAILED,
  LOOKUP_FAILED,
  UNREACHABLE,
  GENERIC_ERROR,
  PASSWORD_MISSING,
  CERTIFICATE_UNAVAILABLE,
}

class FlutterVpn {
  /// Receive state change from VPN service.
  ///
  /// Can only be listened once.
  /// If have more than one subscription, only the last subscription can receive
  /// events.
  static Stream<FlutterVpnState> get onStateChanged => _eventChannel
      .receiveBroadcastStream()
      .map((e) => FlutterVpnState.values[e]);

  /// Get current state.
  static Future<FlutterVpnState> get currentState async => FlutterVpnState
      .values[await _channel.invokeMethod<int>('getCurrentState')];

  /// Get current error state from `VpnStateService`. (Android only)
  /// When [FlutterVpnState.genericError] is received, details of error can be
  /// inspected by [CharonErrorState].
  static Future<CharonErrorState> get charonErrorState async {
    if (!Platform.isAndroid) throw Exception('Unsupport Platform');
    var state = await _channel.invokeMethod<int>('getCharonErrorState');
    return CharonErrorState.values[state];
  }

  /// Prepare for vpn connection. (Android only)
  ///
  /// For first connection it will show a dialog to ask for permission.
  /// When your connection was interrupted by another VPN connection,
  /// you should prepare again before reconnect.
  ///
  /// Do nothing in iOS.
  static Future<bool> prepare() async {
    if (!Platform.isAndroid) return true;
    return await _channel.invokeMethod('prepare');
  }

  static Future<bool> hasPrepared() async {
    if (!Platform.isAndroid) return true;
    final response = await _channel.invokeMethod('hasPrepared');
    return response;
  }

  /// Disconnect and stop VPN service.
  static Future<Null> disconnect() async {
    await _channel.invokeMethod('disconnect');
  }

  static Future<bool> isSecuredWiFi() async {
    return await _channel.invokeMethod('isSecuredWiFi');
  }

  static Future<Null> startMonitor(bool connect) async {
    await _channel.invokeMethod('startMonitoring',connect.toString());
  }

  static Future<Null> stopMonitor() async {
    await _channel.invokeMethod('stopMonitoring');
  }

  /// Connect to VPN.
  ///
  /// Use given credentials to connect VPN (ikev2-eap).
  /// This will create a background VPN service.
  /// MTU is only available on android.
  static Future<Null> simpleConnect(
      String address,
      String username,
      String password,
      String displayName,
      String packageName,
      String activityName,
      {int mtu = 1400}) async {
    await _channel.invokeMethod('connect', {
      'address': address,
      'username': username,
      'password': password,
      'displayName': displayName,
      'packageName': packageName,
      'activityName': activityName,
      'mtu': mtu.toString()
    });
  }

  static Future<Null> unProtectedWiFiConfigure(
      String appName,
      String notificationResourceId,
      String serviceNotificationTitle,
      String serviceNotificationBody,
      String warnTitle,
      String warnBody,
      String autoConnectTitle,
      String autoConnectBody,
      String activityClassName,
      String packageName,
      String userName,
      String displayName,
      String password,
      String address,
      bool isAutoConnect,
      bool isWarn,
      ) async {
    await _channel.invokeMethod('configure', {
      'appName': appName,
      'notificationResourceId': notificationResourceId,
      'serviceNotificationTitle': serviceNotificationTitle,
      'serviceNotificationBody': serviceNotificationBody,
      'warnTitle': warnTitle,
      'warnBody': warnBody,
      'autoConnectTitle': autoConnectTitle,
      'autoConnectBody': autoConnectBody,
      'activityClassName': activityClassName,
      'packageName': packageName,
      'userName': userName,
      'displayName': displayName,
      'password': password,
      'address': address,
      "isAutoConnect": isAutoConnect.toString(),
      "isWarn": isWarn.toString()
    });
  }
}
