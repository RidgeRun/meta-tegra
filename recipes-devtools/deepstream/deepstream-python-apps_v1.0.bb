DESCRIPTION = "NVIDIA Deepstream Python Binding"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2db3a1ac57d81de586d8506c7d63dab9"

SRC_URI= "git://github.com/NVIDIA-AI-IOT/deepstream_python_apps;protocol=git"
SRCREV = "971626b018501db5128d418da304a4c8d38d412b"

S = "${WORKDIR}/git"

DEEPSTREAM_PATH = "/opt/nvidia/deepstream/deepstream-5.0/"

do_install() {
    install -d ${D}${DEEPSTREAM_PATH}/sources/deepstream_python_apps
    cp -R --preserve=mode,timestamps ${S}/* ${D}${DEEPSTREAM_PATH}/sources/deepstream_python_apps
}

FILES_${PN} = "${DEEPSTREAM_PATH}sources/deepstream_python_apps"

RDEPENDS_${PN} = "python3 python3-ctypes"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
