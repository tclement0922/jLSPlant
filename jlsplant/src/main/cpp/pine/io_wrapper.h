/*
 * This file is part of jLSPlant.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>
 *
 * Copyright (C) 2020-2021 canyie <https://github.com/canyie>
 * Copyright (C) 2022 T. Clément <https://github.com/tclement0922>
 */

#ifndef PINE_IO_WRAPPER_H
#define PINE_IO_WRAPPER_H

#include <cstdio>
#include <fcntl.h>
#include <cerrno>
#include "macros.h"
#include "../logging/logging.h"

namespace pine {
    static bool CanRetry(int error) {
        return error == EINTR || error == EIO;
    }

    int WrappedOpen(const char* pathname, int flags, int max_retries = 2) {
        for (;;) {
            int fd = open(pathname, flags);
            if (LIKELY(fd != -1)) {
                return fd;
            }

            if (LIKELY(CanRetry(errno) && max_retries-- > 0)) {
                LOGW("Retrying to open %s with flags %d: errno %d (%s)",
                     pathname, flags, errno, strerror(errno));
            } else {
                LOGE("Failed to open %s with flags %d: errno %d (%s)",
                     pathname, flags, errno, strerror(errno));
                return -1;
            }
        }
    }

    FILE* WrappedFOpen(const char* pathname, const char* mode, int max_retries = 2) {
        for (;;) {
            FILE* file = fopen(pathname, mode);
            if (LIKELY(file)) {
                return file;
            }

            if (LIKELY(CanRetry(errno) && max_retries-- > 0)) {
                LOGW("Retrying to fopen %s with mode %s: errno %d (%s)",
                     pathname, mode, errno, strerror(errno));
            } else {
                LOGE("Failed to fopen %s with mode %s: errno %d (%s)",
                     pathname, mode, errno, strerror(errno));
                return nullptr;
            }
        }
    }
}

#endif //PINE_IO_WRAPPER_H
