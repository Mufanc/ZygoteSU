APK=$(pm path xyz.mufanc.zsu | cut -d : -f 2)
exec /system/bin/app_process -Djava.class.path="$APK" /system/bin xyz.mufanc.zsu.Main "$@"
