// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** ok GET /api/test */
export async function okUsingGet(options?: { [key: string]: any }) {
  return request<string>('/api/test', {
    method: 'GET',
    ...(options || {}),
  });
}
