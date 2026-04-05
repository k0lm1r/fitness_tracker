# JMeter сценарии

В папке лежат два независимых плана:

- `all_endpoints.jmx` - прогон всех endpoint-ов, кроме `/race-condition/demo`.
- `race_condition_only.jmx` - нагрузка только на `/race-condition/demo`.
Оба плана выполняют регистрацию/логин и автоматически подставляют `Authorization: Bearer ...` для защищённых endpoint-ов.

## Что нужно перед запуском

1. Поднять приложение (`localhost:8080` по умолчанию).
2. Для `POST /media` в плане используется файл `jmeter/assets/upload-sample.txt`.
3. При необходимости переопределить параметры через `-J`:
   - `host`
   - `port`
   - `protocol`
   - `basePath`
   - `dayDate`
   - `raceUsers`
   - `raceRampUpSeconds`
   - `raceLoops`

## Примеры запуска

```bash
jmeter -n -t jmeter/all_endpoints.jmx -l jmeter/results/all_endpoints.jtl
```

```bash
jmeter -n -t jmeter/race_condition_only.jmx -l jmeter/results/race_only.jtl -JraceUsers=100 -JraceLoops=20
```
