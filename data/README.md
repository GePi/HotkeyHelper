# HotKey Helper — Конфигурация

Эта папка содержит конфигурацию маппинга приложений и HTML-файлы с горячими клавишами.

## Структура

```
data/
├── config.json          # Маппинг: процесс -> HTML-файл
├── shortcuts/           # HTML-файлы с горячими клавишами
│   ├── chrome.html
│   ├── vscode.html
│   ├── idea.html
│   └── 1c.html
└── README.md
```

## Формат config.json

```json
{
  "apps": [
    {
      "processName": "chrome.exe",
      "windowTitle": null,
      "htmlFile": "chrome.html"
    },
    {
      "processName": "1cv8.exe",
      "windowTitle": "(Конфигуратор|Отладка)",
      "htmlFile": "1c.html"
    }
  ]
}
```

### Поля

| Поле          | Обязательно | Описание                                            |
|---------------|-------------|-----------------------------------------------------|
| processName   | да          | Имя процесса, например `chrome.exe`, `1cv8.exe`. Сравнение регистронезависимое |
| windowTitle   | нет         | Регулярное выражение для фильтрации по заголовку окна. `null` — любой заголовок |
| htmlFile      | да          | Имя HTML-файла в папке `shortcuts/`                 |

### Фильтрация по заголовку окна (windowTitle)

Поле `windowTitle` поддерживает регулярные выражения Java (case-insensitive). Поиск выполняется через `find()` — достаточно частичного совпадения.

| Значение                         | Поведение                                              |
|----------------------------------|--------------------------------------------------------|
| `null`                           | Совпадение по любому заголовку                         |
| `"Конфигуратор"`                 | Заголовок содержит "Конфигуратор" (в любом регистре)   |
| `"^1С:Предприятие"`              | Заголовок начинается с "1С:Предприятие"                |
| `"(Отладка\|Конфигуратор)"`      | Заголовок содержит "Отладка" или "Конфигуратор"        |
| `".*\\.txt$"`                    | Заголовок заканчивается на ".txt"                      |

Если regex невалиден, используется простой поиск подстроки (fallback на `contains`).

### Порядок приоритета

Записи в `config.json` проверяются сверху вниз. Первое совпадение побеждает. Это позволяет задавать специфичные правила перед общими:

```json
{
  "apps": [
    {
      "processName": "1cv8.exe",
      "windowTitle": "Отладка",
      "htmlFile": "1c-debug.html"
    },
    {
      "processName": "1cv8.exe",
      "windowTitle": null,
      "htmlFile": "1c.html"
    }
  ]
}
```

В этом примере окна 1С с "Отладка" в заголовке получат `1c-debug.html`, все остальные окна 1С — `1c.html`.

## Формат HTML-файлов

HTML-файлы должны быть самодостаточными — все стили встроены. Приложение добавляет поверх свою CSS-тему (тёмный фон, цвет текста, скроллбары), но собственные стили файла имеют приоритет.

### Шаблон

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            background-color: #1e1e2e;
            color: #cdd6f4;
            margin: 0;
            padding: 15px;
        }
        h2 {
            color: #89b4fa;
            font-size: 18px;
            margin-top: 20px;
            margin-bottom: 10px;
            border-bottom: 2px solid #45475a;
            padding-bottom: 5px;
        }
        .shortcut {
            display: flex;
            justify-content: space-between;
            padding: 8px 10px;
            margin: 5px 0;
            background-color: #313244;
            border-radius: 6px;
            align-items: center;
        }
        .shortcut:hover {
            background-color: #45475a;
        }
        .keys {
            font-family: 'Consolas', 'Courier New', monospace;
            background-color: #585b70;
            padding: 4px 10px;
            border-radius: 4px;
            font-weight: bold;
            color: #cdd6f4;
            white-space: nowrap;
        }
        .description {
            flex: 1;
            margin-right: 15px;
            color: #bac2de;
        }
    </style>
</head>
<body>
    <h2>Название секции</h2>
    <div class="shortcut">
        <span class="description">Описание действия</span>
        <span class="keys">Ctrl+C</span>
    </div>
</body>
</html>
```

### Цветовая схема (Catppuccin Mocha)

| Элемент              | Цвет      | Назначение                      |
|----------------------|-----------|---------------------------------|
| Фон страницы         | `#1e1e2e` | Основной фон                    |
| Фон карточки клавиши | `#313244` | Фон строки с шорткатом          |
| Фон при наведении    | `#45475a` | Hover-эффект                    |
| Фон клавиши          | `#585b70` | Бейдж с комбинацией клавиш      |
| Текст                | `#cdd6f4` | Основной текст                  |
| Описание             | `#bac2de` | Текст описания действия         |
| Заголовок секции     | `#89b4fa` | Цвет h2                         |
| Разделитель          | `#45475a` | Линия под заголовком секции      |

## Добавление нового приложения

1. Запустите HotKey Helper и откройте нужное приложение — в статус-баре отобразится имя процесса.
2. Скопируйте шаблон HTML выше в новый файл в папке `shortcuts/`.
3. Заполните секции и горячие клавиши.
4. Добавьте запись в `config.json`.
5. Перезапустите HotKey Helper.
