#pragma once

#include <cuda_runtime.h>
#include "structure/memory/GPUHashConstants.h"

namespace bitgraph {
    namespace memory {

        __device__ inline size_t raw_hash(size_t key) {
            return key * (size_t)floor((double)BITGRAPH_HASH_MAGIC * pow((double)2, (double)64));
        }

        __device__ inline size_t bitgraph_hash_fn(size_t key, size_t table_size, size_t iters, size_t max_chain_iters) {
            // rehash until max iters, then linear probe
            return  (iters < max_chain_iters)
                    ? raw_hash(key) % table_size
                    : (key + 1) % table_size;
        }

        inline size_t bitgraph_get_max_permitted_hash_iterations(size_t table_size) {
            return static_cast<size_t>(1 + 3 * log10f64(table_size));
        }

        inline size_t bitgraph_get_max_permitted_chain_hash_iterations(size_t table_size) {
            return static_cast<size_t>(1 + log10f64(table_size));
        }
 
    }
}