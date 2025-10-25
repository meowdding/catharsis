---
title: Area Selection
lang: en-US
---

# Area Selection

The area selection tool can be used to create bounding boxes for things like ore veins.

### Usage

The tool operates with filters, these can be edited with `/catharsis dev area_selection add/remove <block>`. Running that command will add/remove a block to the current filter list.

To see all currently allowed blocks you can run `/catharsis dev area_selection list`.

The area selection itself can be dispatched via `/catharsis dev area_selection <range>`, range being optional, defaulting to 100.
