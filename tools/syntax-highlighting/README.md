# OTG Config Syntax Highlighting

Highlighting for OTG's plain-text config format (`.bc`, `.bo2`/`.bo3`/`.bo4`, and preset `.ini` files):
`#` comments, `Key: Value` settings, `Function(arg1, arg2, ...)` resource entries, numbers, hex colors
(`0xFA9418`), booleans, and `minecraft:...` resource locations.

## VS Code

`vscode/otg-config/` is a complete extension. Install by copying it into your extensions folder.

Then reload VS Code. `.bc`/`.bo*` files are picked up by extension; `.ini` files are only claimed when
inside a `Presets/` or `DimensionConfigs/` folder, so it won't hijack unrelated INI files.

Alternatively, package it properly with [`vsce`](https://code.visualstudio.com/api/working-with-extensions/publishing-extension):
`npx @vscode/vsce package` inside `vscode/otg-config/`, then install the generated `.vsix` via
*Extensions → ... → Install from VSIX*.

## IntelliJ IDEA

IDEA consumes the same TextMate grammar:

1. Make sure the bundled **TextMate Bundles** plugin is enabled.
2. *Settings → Editor → TextMate Bundles → + →* select the `vscode/otg-config` directory.
3. If `.bc` is already mapped to a file type (e.g. plain text), remove that association under
   *Settings → Editor → File Types* so the TextMate bundle can claim it.

For OTG `.ini` files, IDEA's built-in INI highlighting also works reasonably; the TextMate bundle only
applies where its `filenamePatterns` match.

## Notepad++

1. *Language → User Defined Language → Define your language... → Import...*
2. Select `notepad++/OTG-Config-UDL.xml`.
3. Restart Notepad++. `.bc`/`.bo2`/`.bo3`/`.bo4` files highlight automatically; for `.ini` files pick
   *Language → OTG Config* manually (UDL extension mapping is global, and claiming `.ini` would affect
   all INI files).

Note: Notepad++ UDL is word-list based, so function names are a fixed list (edit the `Keywords2` list in
the XML to add more). The TextMate grammar highlights any `Name(...)` pattern generically.
