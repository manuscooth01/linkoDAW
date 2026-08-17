# linkoDAW - DAW gratuito para Android

## Contexto del Proyecto

linkoDAW es una aplicación de estudio de audio digital (DAW) para Android, similar a BandLab pero completamente gratuita, sin membresías ni límites de pago. El objetivo es ofrecer funcionalidades profesionales (grabación multipista, efectos, mezcla, herramientas de IA) sin coste para el usuario.

## Stack Tecnológico

- **Lenguaje**: Kotlin 1.9.10
- **Min SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Arquitectura**: Clean Architecture + MVVM
- **DI**: Hilt 2.48
- **Async**: Coroutines + Flow
- **UI**: ViewBinding + DataBinding + Material 3
- **Testing**: JUnit 4, Mockito, Espresso
- **Gradle**: 8.2, AGP 8.1.4

## Estructura del Proyecto

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/linkodaw/
│   │   │   ├── data/           # Capa de datos
│   │   │   │   ├── audio/      # Implementación de audio (AudioRecord/AudioTrack)
│   │   │   │   └── repository/ # Repositorios
│   │   │   ├── domain/         # Capa de dominio
│   │   │   │   ├── model/      # Entidades y modelos
│   │   │   │   ├── repository/ # Interfaces de repositorio
│   │   │   │   └── usecase/    # Casos de uso
│   │   │   ├── presentation/   # Capa de presentación
│   │   │   │   └── main/       # MainActivity, ViewModel, Adapter
│   │   │   └── di/             # Módulos de Hilt
│   │   ├── res/                # Recursos (layouts, values, drawables)
│   │   └── AndroidManifest.xml
│   └── test/                   # Tests unitarios
├── build.gradle
└── proguard-rules.pro
```

## Comandos de Desarrollo

```bash
# Compilar APK debug
./gradlew assembleDebug

# Ejecutar tests unitarios
./gradlew testDebugUnitTest

# Ejecutar lint
./gradlew lintDebug

# Limpiar proyecto
./gradlew clean

# Compilar AAB release (requiere signing config)
./gradlew bundleRelease
```

## Arquitectura - Clean Architecture

### Capas

1. **Domain** (Pura Kotlin, sin dependencias Android)
   - Entidades: `Track`, `AudioState`
   - Interfaces: `TrackRepository`, `AudioRecorder`, `AudioPlayer`
   - Casos de uso: `RecordAudioUseCase`, `PlayAudioUseCase`, `GetTracksUseCase`, etc.

2. **Data** (Implementaciones con dependencias Android)
   - `AudioRecorderImpl` - Usa `AudioRecord` para grabación de bajo nivel
   - `AudioPlayerImpl` - Usa `AudioTrack` para reproducción de bajo nivel
   - `TrackRepositoryImpl` - Persistencia en archivos locales

3. **Presentation** (Android Framework)
   - `MainViewModel` - Estado reactivo con StateFlow
   - `MainActivity` - UI con ViewBinding
   - `TracksAdapter` - RecyclerView para lista de pistas

### Inyección de Dependencias (Hilt)

Módulos en `di/`:
- `AudioModule` - `@ActivityRetainedScoped` para AudioRecorder/Player
- `RepositoryModule` - `@Singleton` para TrackRepository
- `UseCaseModule` - Casos de uso

## Audio - Implementación de Bajo Nivel

### Grabación (AudioRecord)
- Sample rate: 44.1 kHz
- Formato: PCM 16-bit mono
- Buffer: 4x tamaño mínimo
- Hilo dedicado para escritura continua

### Reproducción (AudioTrack)
- MODE_STREAM para streaming
- Seek preciso por frames
- Pausa/reanudación sin glitches

### Service en Primer Plano
- `AudioRecordingService` para grabación en background
- Notificación persistente con controles

## Permisos

Manejo moderno con Activity Result APIs:
- `RECORD_AUDIO` - Obligatorio para grabar
- `WRITE_EXTERNAL_STORAGE` (API ≤ 28) - Para guardar en Music/
- `READ_EXTERNAL_STORAGE` (API ≤ 32) - Para leer pistas
- `MANAGE_EXTERNAL_STORAGE` (API 30+) - Acceso amplio si es necesario

## CI/CD - GitHub Actions

Workflow en `.github/workflows/android-ci.yml`:
- **build**: Compila APK debug en cada push/PR
- **test**: Ejecuta tests unitarios
- **lint**: Análisis estático
- **release-build**: Genera AAB firmado en push a main

Artefactos:
- `linkodaw-debug-apk` - APK descargable (7 días)
- `test-reports` - Reportes de tests
- `lint-report` - Reporte de lint
- `linkodaw-release-aab` - AAB para Play Store (30 días)

## Próximos Pasos (Roadmap)

### Fase 1 - MVP ✅ (Actual)
- [x] Estructura base Clean Architecture
- [x] Hilt configurado
- [x] Grabación/reproducción PCM básica
- [x] UI principal con controles
- [x] Lista de pistas
- [x] CI/CD GitHub Actions

### Fase 2 - Funcionalidades Core
- [ ] Waveform visual durante grabación/reproducción
- [ ] Ecualizador básico (bass/mid/treble)
- [ ] Control de volumen y pan por pista
- [ ] Metrónomo
- [ ] Exportar a WAV/MP3/OGG

### Fase 3 - Multipista
- [ ] Mezclador con múltiples pistas simultáneas
- [ ] Solo/Mute por pista
- [ ] Automación de volumen
- [ ] Time-stretching y pitch-shifting

### Fase 4 - Efectos y IA
- [ ] Reverb, Delay, Compresión
- [ ] Separación de stems (IA)
- [ ] Transcripción de audio a MIDI
- [ ] Generación de acompañamiento (IA)

### Fase 5 - Cloud & Colaboración
- [ ] Sincronización en la nube
- [ ] Colaboración en tiempo real
- [ ] Compartir proyectos

## Notas para Futuros Agentes

### Al añadir nuevas features:
1. Crear entidad en `domain/model/`
2. Definir interface de repositorio en `domain/repository/`
3. Implementar en `data/repository/`
4. Crear caso de uso en `domain/usecase/`
5. Añadir módulo Hilt si es necesario en `di/`
6. Exponer StateFlow en ViewModel
7. Actualizar UI

### Convenciones de código:
- Usar corrutinas y Flow para async
- StateFlow para estado UI, SharedFlow para eventos
- Inyección por constructor con @Inject
- Nombres descriptivos en español para dominio, inglés para técnico
- Tests unitarios para casos de uso y repositorios

### Debugging audio:
- Logs en `AudioRecorderImpl` y `AudioPlayerImpl`
- Verificar permisos en settings de la app
- Revisar notification channel para foreground service

## Configuración de Firma (Release)

Para builds de release, configurar en GitHub Secrets:
- `SIGNING_KEY_ALIAS` - Alias de la clave
- `SIGNING_KEY_PASSWORD` - Password de la clave
- `SIGNING_STORE_PASSWORD` - Password del keystore
- Subir keystore como base64 en `KEYSTORE_BASE64`

## Contacto y Recursos

- Documentación Android Audio: https://developer.android.com/guide/topics/media/audio
- Hilt: https://developer.android.com/training/dependency-injection/hilt-android
- Coroutines: https://developer.android.com/kotlin/coroutines
- Material 3: https://m3.material.io/