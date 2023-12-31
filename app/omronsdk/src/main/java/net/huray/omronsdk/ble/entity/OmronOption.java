package net.huray.omronsdk.ble.entity;

import android.os.Bundle;

import net.huray.omronsdk.androidcorebluetooth.CBConfig;
import net.huray.omronsdk.ble.OHQConfig;
import net.huray.omronsdk.ble.enumerate.OHQSessionOptionKey;
import net.huray.omronsdk.ble.enumerate.OHQSessionType;
import net.huray.omronsdk.utility.Bundler;

import java.util.HashMap;
import java.util.Map;

public class OmronOption {
    private static final int CONSENT_CODE_OHQ = 0x020E;

    public static Bundle getConfig() {
        CBConfig.CreateBondOption cOption = CBConfig.CreateBondOption.UsedAfterServicesDiscovered;
        CBConfig.RemoveBondOption rOption = CBConfig.RemoveBondOption.NotUse;

        return Bundler.bundle(
                CBConfig.Key.CreateBondOption.name(), cOption,
                CBConfig.Key.RemoveBondOption.name(), rOption,
                CBConfig.Key.AssistPairingDialogEnabled.name(), false,
                CBConfig.Key.AutoPairingEnabled.name(), false,
                CBConfig.Key.AutoEnterThePinCodeEnabled.name(), false,
                CBConfig.Key.PinCode.name(), "000000",
                CBConfig.Key.StableConnectionEnabled.name(), true,
                CBConfig.Key.StableConnectionWaitTime.name(), 1500L,
                CBConfig.Key.ConnectionRetryEnabled.name(), true,
                CBConfig.Key.ConnectionRetryDelayTime.name(), 1000L,
                CBConfig.Key.ConnectionRetryCount.name(), 0,
                CBConfig.Key.UseRefreshWhenDisconnect.name(), true
        );
    }

    /**
     * 체성분계, 혈압계 공통 옵션 설정
     * */
    public static Map<OHQSessionOptionKey, Object> getOptionsKeys(int waitTimeSeconds) {
        final long waitTimeMillis = waitTimeSeconds * 1000L;

        Map<OHQSessionOptionKey, Object> options = new HashMap<>();
        options.put(OHQSessionOptionKey.ConsentCodeKey, CONSENT_CODE_OHQ);
        options.put(OHQSessionOptionKey.ConnectionWaitTimeKey, waitTimeMillis);
        options.put(OHQSessionOptionKey.ReadMeasurementRecordsKey, true);

        return options;
    }

    public static Map<OHQSessionOptionKey, Object> getWeightOptionsKeys(WeightDeviceInfo info, int waitTimeSeconds) {
        Map<OHQSessionOptionKey, Object> options = getOptionsKeys(waitTimeSeconds);
        setWeightDeviceOptionKeys(options, info);

        return options;
    }

    /**
     * 체성분계 옵션을 추가
     */
    private static void setWeightDeviceOptionKeys(Map<OHQSessionOptionKey, Object> options, WeightDeviceInfo info) {
        if (info.getSessionType() == OHQSessionType.REGISTER) {
            options.put(OHQSessionOptionKey.RegisterNewUserKey, true);
            options.put(OHQSessionOptionKey.DatabaseChangeIncrementValueKey, 0L);
        } else {
            setTransferOptionKey(options, info);
        }

        options.put(OHQSessionOptionKey.UserIndexKey, info.getIndex());
        options.put(OHQSessionOptionKey.UserDataKey, info.getUserData());
        options.put(OHQSessionOptionKey.UserDataUpdateFlagKey, true);
        options.put(OHQSessionOptionKey.AllowAccessToOmronExtendedMeasurementRecordsKey, true);
        options.put(OHQSessionOptionKey.AllowControlOfReadingPositionToMeasurementRecordsKey, true);
    }

    /**
     * 오므론 체성분계 HBF-222T 데이터 전송시 사용하는 옵션을 추가
     *
     * @SequenceNumberOfFirstRecordToReadKey 이걸 사용하지 않으면 항상 체성분계에 저장된 모든 기록을 가져옴
     * 예를 들어 방금 체성분계에 101번째 기록이 정되었다면 sequenceNumber로 101을 전달해줘서 앱에 저장되지 않은 최근
     * 기록만 요청함
     * @DatabaseChangeIncrementValueKey 체성분계에 저장된 사용자 정보를 변경하는데 사용
     * 체성분계에서 수신된 데이터에 포함된 사용자 정보와 앱의 사용자 정보를 비교하여 일치하지 않으면 다음 요청시 체성분계의
     * 사용자 정보를 수정함
     * DatabaseIncrementKey에 +1한 값을 전달해주면 됨
     */
    private static void setTransferOptionKey(Map<OHQSessionOptionKey, Object> options, WeightDeviceInfo info) {
        if (info.getSequenceNumber() != -1) {
            options.put(OHQSessionOptionKey.SequenceNumberOfFirstRecordToReadKey, info.getSequenceNumber() + 1);
        }

        options.put(OHQSessionOptionKey.DatabaseChangeIncrementValueKey, info.getIncrementKey());
    }
}
