DESCRIPTION = "NVIDIA Deepstream SDK"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/share/doc/deepstream-5.0/copyright;md5=f635f9f375e764ce281a2070599e2457"

SRC_URI="https://repo.download.nvidia.com/jetson/common/pool/main/d/deepstream-5.0/deepstream-5.0_5.0.0-1_arm64.deb;subdir=deepstream-${PV}"
SRC_URI[sha256sum] = "a7a7015515883ac88c7587c7a2acfcf78510e539b84b702afd05f4f330faa55e"

COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

DEPENDS = "gstreamer1.0 gstreamer1.0-rtsp-server tensorrt cudnn libcublas cuda-cudart tegra-libraries"

inherit pkgconfig python3native

B = "${WORKDIR}/build"
DEEPSTREAM_PATH = "/opt/nvidia/deepstream/deepstream-5.0"
SYSROOT_DIRS += "${DEEPSTREAM_PATH}/lib/"
DISTUTILS_INSTALL_ARGS ?= "--root=${D} \
    --prefix=${prefix} \
    --install-lib=${PYTHON_SITEPACKAGES_DIR} \
    --install-data=${datadir}"

do_compile() {
   cd ${S}/${DEEPSTREAM_PATH}/lib/
   NO_FETCH_BUILD=1 \
   STAGING_INCDIR=${STAGING_INCDIR} \
   STAGING_LIBDIR=${STAGING_LIBDIR} \
   ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} ${S}${DEEPSTREAM_PATH}/lib/setup.py \
   build --build-base=${B} ${DISTUTILS_BUILD_ARGS} || \
   bbfatal_log "'${PYTHON_PN} setup.py build ${DISTUTILS_BUILD_ARGS}' execution failed."
}

do_install() {
    install -d ${D}${bindir}/
    install -m 0755 ${S}${DEEPSTREAM_PATH}/bin/* ${D}${bindir}/

    install -d ${D}${DEEPSTREAM_PATH}/lib/
    for f in ${S}${DEEPSTREAM_PATH}/lib/*; do
        [ ! -d "$f" ] || continue
        install -m 0644 "$f" ${D}${DEEPSTREAM_PATH}/lib/
    done
    
    install -d ${D}${DEEPSTREAM_PATH}/lib/tensorflow
    install -m 0644 ${S}${DEEPSTREAM_PATH}/lib/tensorflow/* ${D}${DEEPSTREAM_PATH}/lib/tensorflow

    install -d ${D}/etc/ld.so.conf.d/
    echo "${DEEPSTREAM_PATH}/lib" > ${D}/etc/ld.so.conf.d/deepstream.conf

    install -d ${D}${libdir}/gstreamer-1.0/deepstream
    install -m 0644 ${S}${DEEPSTREAM_PATH}/lib/gst-plugins/* ${D}${libdir}/gstreamer-1.0/deepstream/

    cp -R --preserve=mode,timestamps ${S}${DEEPSTREAM_PATH}/samples ${D}${DEEPSTREAM_PATH}/

    install -d ${D}${includedir}/deepstream
    cp -R --preserve=mode,timestamps ${S}${DEEPSTREAM_PATH}/sources/includes/* ${D}${includedir}/

    cp -R --preserve=mode,timestamps ${S}${DEEPSTREAM_PATH}/sources/ ${D}${DEEPSTREAM_PATH}/

    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    STAGING_INCDIR=${STAGING_INCDIR} \
    STAGING_LIBDIR=${STAGING_LIBDIR} \
    PYTHONPATH=${D}${PYTHON_SITEPACKAGES_DIR} \
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} ${S}${DEEPSTREAM_PATH}/lib/setup.py \
    build --build-base=${B} install --skip-build ${DISTUTILS_INSTALL_ARGS} || \
    bbfatal_log "'${PYTHON_PN} setup.py install ${DISTUTILS_INSTALL_ARGS}' execution failed."
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

RDEPENDS_${PN} = "glib-2.0 gstreamer1.0 libgstvideo-1.0 libgstrtspserver-1.0 libgstapp-1.0 json-glib"
RDEPENDS_${PN}-samples = "bash json-glib"
RDEPENDS_${PN}-sources = "bash"

PACKAGES += "${PN}-samples ${PN}-sources ${PN}-python"

FILES_${PN} = "/etc/ld.so.conf.d/  \
	       ${libdir}/gstreamer-1.0/deepstream \
	       ${DEEPSTREAM_PATH}/lib \
	      "

FILES_${PN}-dev = "${includedir}"

FILES_${PN}-samples = "${bindir}/* ${DEEPSTREAM_PATH}/samples \
		       ${DEEPSTREAM_PATH}/sources/apps/sample_apps/*/*.txt \
		       ${DEEPSTREAM_PATH}/sources/apps/sample_apps/deepstream-test5/configs/"

FILES_${PN}-sources = "${DEEPSTREAM_PATH}/sources"

FILES_${PN}-python =  "${libdir}/${PYTHON_DIR}/*"
