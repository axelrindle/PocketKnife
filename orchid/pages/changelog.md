> *Generated by [git-changelog-generator](https://github.com/axelrindle/git-changelog-generator)*

## 2.0.0 `(09.07.2020)`

- Include help from subCommands by default
- Update PocketConfig.kt
- Add docs
- Fix magic numbers
- Add documentation
- Update annotations
- Update handling of subcommands
- Add permission checks
- Send message color formatted
- Remove unused suppression
- Fix weird object formatting
- Make sure the correct inventory has been clicked
- Add method getItem()
- Remove useless UUID lookup tasks
- Simplify the permission nullity check
- Simplify PocketCommand registration
- Add possibility to modify the Inventory before showing it to a player
- Replace getSubCommands() method with a class attribute
- Cancel the event after listener invocation
- Update docs
- Add shorthand method for updating ItemMetas
- Remove Suppress annotation
- Deprecate the custom UUID lookup
- Change file modes
- Update documentation

## 1.3.0 `(16.06.2019)`

- Split up project into api and plugin
- Move extensions to top-level
- Consider custom completion not only without sub-commands
- Use helper method to format colors
- Implement the player check
- Add method to reload all config files at once
- Add some missing javadocs
- World name is optional
- Add util class for working with UUIDs
- Implement fully functional tab completion
- Suppress unused warnings
- The defaults InputStream can be null
- Return the subcommand result instead of breaking
- Ignore command case
- Provide default implementation for sendHelp
- Create utility classes
- Specify api version
- Fix errors caused by updated kotlin version
- Implement player requirement check and support normal handling beside sub-commands. Closes #8, #9
- Calculate highest index
- Move initialization to field block

## v1.2.1 `(09.09.2018)`

- Fix supply of command arguments
- Only check for sub commands if there are arguments supplied
- Arrays start at index 0 :man_facepalming:
- Only create parent directories if the file has a parent that is not equal to the plugin's data directory

## v1.2.0 `(25.08.2018)`

- Replace permission test methods with "custom" implementations making use of the helper functions
- Make getters open as sub-commands will likely have no entry in the plugin.yml file. Closes #2
- Test for permission before executing a sub-command. Closes #1

## v1.1.0 `(11.08.2018)`

- Add disabled message
- Create a plugin file to avoid duplicate class errors
- Suppress some warnings

## v1.0.0 `(18.06.2018)`

- Add PocketLang.kt
- Create all parent directories of the given file
- Close defaults stream after closing the reader
- Add method for creating an ItemStack
- Add documentation
- Add PocketInventory.kt and InventoryUtils.kt
- Break up complex method
- Add PocketCommand.kt and PocketConfig.kt