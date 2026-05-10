---
title: .cats File Format
lang: en-US
---

# .cats File Format

.cats (`(c)atharsis (a)rchive (t)ool (s)tructure`) is our custom file format to store resourcepacks a bit more efficiently than a simple zip file and loading it faster.

[File Format Repo](https://github.com/meowdding/cats-file-format) -
[Pack Squash Tool](https://github.com/meowdding/catsquash) - 
[Pack Squash Website](https://meowdd.ing/catpack) <!--TODO CONFIRM URL-->

## Publishing to Modrinth

Since Modrinth only allows .zip files to be uploaded, you can put the .cats file inside a .zip file and naming the zip `<>.cats.zip`when publishing to Modrinth.
You can also add pack.mcmeta and the icon alongside the .cats in the zip which appear when catharsis isn't installed, 
the pack.mcmeta and the icon inside the .cats get prioritized.

````
YourPack.cats.zip
├── YourPack.cats
│   ├── pack.png      # Shows when catharsis is installed
│   └── pack.mcmeta   # Shows when catharsis is installed
├── pack.png          # Shows when catharsis is NOT installed, shows in the Modrinth/Prism Launcher
└── pack.mcmeta       # Shows when catharsis is NOT installed, shows in the Modrinth/Prism Launcher

````

## Format


| Field  | Type   | Description                       |
|--------|--------|-----------------------------------|
| Header | Header | The file header                   |
| Data   | Byte[] | The raw data of files/directories |

### Header

| Field          | Type      | Description                |
|----------------|-----------|----------------------------|
| Magic Number   | Int       | Always 0x43415453 = "CATS" |
| Version        | UByte     | Version (currently 0x01)   |
| Root Directory | Directory | The root directory         |

### Directory

| Field       | Type    | Description                        |
|-------------|---------|------------------------------------|
| Entry Count | UShort  | Number of entries in the directory |
| Entries     | Entry[] | Array of entries                   |

### File

| Field       | Type  | Description                                 |
|-------------|-------|---------------------------------------------|
| Offset      | Int   | Offset from the header to data              |
| Size        | Int   | Size of the file data                       |
| Compression | UByte | Compression type (0xFF = None, 0xFE = GZIP) |

### Entry

| Field       | Type              | Description                                                            |
|-------------|-------------------|------------------------------------------------------------------------|
| Entry Type  | UByte             | Type of entry (0x00 = File, 0x01 = Directory)                          |
| Name Length | UByte             | Length of the name string                                              |
| Name        | UByte[]           | ASCII name of the entry, Only 0x21-0x7E excluding / and \ are allowed. |
| Entry Data  | Directory or File | Data specific to the entry type                                        |

[From...](https://github.com/meowdding/cats-file-format/blob/main/format.md)
