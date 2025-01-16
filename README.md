# AO Anisotropy Fix

Fix ambient occlusion looking different depending on the rotation or orientation
of a block face quad. Both the Batched and Naive renderers are supported. Report
any issues with missing chunks, a quick fix is to switch to a different renderer
and back again.

| With Mod Installed | Cosmic Reach as of 0.3.16 |
|:------------------:|:-------------------------:|
|    ![with-mod]     |      ![without-mod]       |

[with-mod]: assets/with-mod.png
[without-mod]: assets/without-mod.png

## Downloads

AO Anisotropy Fix is only officially available on CRMM and GitHub. All
published version of this mod are both in
[CRMM](https://www.crmm.tech/mod/ao-anisotropy-fix/versions) and [GitHub Releases](https://github.com/StartsMercury/ao-anisotropy-fix/releases):

> * <https://www.crmm.tech/mod/ao-anisotropy-fix/versions>
> * <https://github.com/StartsMercury/ao-anisotropy-fix/releases>

---

This repository is generated from
https://codeberg.org/CRModders/cosmic-quilt-example.

## Wiki

For a wiki on how to use Cosmic Quilt & Quilt, please look at the [Cosmic Quilt
wiki] .

## How to test/build

For testing in the developer environment, you can use the `./gradlew run` task

For building, the usual `./gradlew build` task can be used. The mod jars will be
in the `build/libs/` folder

## Notes
- Most project properties can be changed in the <tt>[gradle.properties]</tt>
- To change author, description and stuff that is not there, edit <tt>[src/main/resources/quilt.mod.json]</tt>
- The project name is defined in <tt>[settings.gradle.kts]</tt>
- To add Quilt mods in the build, make sure to use `internal` rather than `implementation`

[src/main/resources/quilt.mod.json]: src/main/resources/quilt.mod.json
[gradle.properties]: gradle.properties
[settings.gradle.kts]: settings.gradle.kts

[Cosmic Quilt wiki]: https://codeberg.org/CRModders/cosmic-quilt/wiki
