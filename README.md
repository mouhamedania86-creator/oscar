# AlphaFlix

تطبيق بث Android — أفلام، مسلسلات، قنوات مباشرة.

## ما تم إصلاحه (بعد probe على Termux)

### مسارات API الحقيقية
| الوظيفة | المسار |
|---------|--------|
| قائمة أفلام | `api/movies/` |
| تفاصيل فيلم | `api/movies/show.php?id=` |
| قائمة مسلسلات | `api/series/` |
| تفاصيل مسلسل | `api/series/show.php?id=` |
| حلقات | `api/anime/episodes/?season_id=` |
| قائمة قنوات | `api/channels/` |
| تفاصيل قناة + بث | `api/channels/show.php?id=` → `streams[]` |

### روابط التشغيل
- فيلم: `data.watch_links[].url`
- قناة: `data.streams[].stream_url`
- CDN `cdn.mediaoscar.com` يقدّم m3u8 حتى لو الامتداد `.css`
- الـ Player يعامل `.css` و `.m3u8` كـ HLS

### Iron
- التوقيع وحده يكفي للقوائم والتفاصيل
- Bearer ما زال يُرسل للتوافق مع الـ traffic الأصلي

## البنية

```
AlphaFlix/
├── settings.gradle / build.gradle / gradle.properties
├── app/src/main/java/com/alphaflix/app/
│   ├── api/     Iron + Retrofit + Repository
│   ├── models/  MediaItem, WatchLink, StreamLink, Season…
│   └── ui/      Home, Movies, Series, Channels, Detail, Player
└── .github/workflows/build.yml
```

## التشغيل

1. افتح المجلد في Android Studio
2. Sync Gradle
3. Run (minSdk 24)

## Credentials

`ApiConstants.kt` — من traffic التطبيق الأصلي. إذا انتهى التوكن، جدّده.

## GitHub Actions

ادفع على `main` أو شغّل workflow يدوياً → APK في Artifacts.
