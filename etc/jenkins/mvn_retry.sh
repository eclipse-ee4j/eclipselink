#!/bin/bash
#
# Copyright (c) 2026 Contributors to the Eclipse Foundation
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause

#
# Runs Maven and runs it again when it failed only because a remote repository
# could not be reached (e.g. "Could not transfer artifact ... Read timed out").
# Any other failure, such as a compile error or failing test, is returned at once.
#
# Arguments:
#  Maven arguments, passed on unchanged
#
# Environment:
#  MVN_RETRY_ATTEMPTS - total number of attempts (default 3)
#  MVN_RETRY_DELAY    - seconds to wait between attempts (default 60)

set -o pipefail

ATTEMPTS=${MVN_RETRY_ATTEMPTS:-3}
DELAY=${MVN_RETRY_DELAY:-60}
LOG=$(mktemp)
trap 'rm -f "${LOG}"' EXIT

# Only Maven's own error lines count, so a test logging a timeout does not trigger a retry.
TRANSFER_ERROR='^\[(ERROR|FATAL)\].*Could not (transfer|download)'
NETWORK_CAUSE='timed out|Connect to .* failed|Connection reset|Connection refused|handshake|Unknown host|status code: 5[0-9][0-9]'

ATTEMPT=1
EXTRA_ARGS=()
while true; do
    mvn "${EXTRA_ARGS[@]}" "$@" 2>&1 | tee "${LOG}"
    STATUS=$?
    if [ ${STATUS} -eq 0 ]; then
        exit 0
    fi
    if [ ${ATTEMPT} -ge ${ATTEMPTS} ] || ! grep -E "${TRANSFER_ERROR}" "${LOG}" | grep -Eq "${NETWORK_CAUSE}"; then
        exit ${STATUS}
    fi
    echo "-[ Maven could not reach a repository (attempt ${ATTEMPT} of ${ATTEMPTS}), retrying in ${DELAY}s ]-"
    sleep "${DELAY}"
    ATTEMPT=$((ATTEMPT + 1))
    # Maven caches failed transfers in the local repository; -U makes it try them again.
    EXTRA_ARGS=(-U)
done
