import '@/common/components/settings/style.scss';
import PreApplyButton from "./buttons/PreApplyButton";
import ApplyButton from "./buttons/ApplyButton";

const SettingActivity = () => {

    return (
        <div className="setting-activity">
            <h2 className="setting-activity__title">Настройки</h2>

            <div className="setting-activity__content">
                <ApplyButton />

                <PreApplyButton />
            </div>
        </div>
    )
};

export default SettingActivity;