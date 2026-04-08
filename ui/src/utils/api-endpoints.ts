const BASE = process.env.NEXT_PUBLIC_API_URL!;

export const API = {
  auth: {
    login: `${BASE}/auth/login`,
    refresh: `${BASE}/auth/refresh`,
  },
  user: {
    list: `${BASE}/user`,
    byUserName: (userName: string) => `${BASE}/user/${userName}`,
    create: `${BASE}/user`,
    update: `${BASE}/user`,
    delete: (userName: string) => `${BASE}/user/${userName}`,
  },
  project: {
    list: `${BASE}/project`,
    byCode: (code: string) => `${BASE}/project/${code}`,
    create: `${BASE}/project`,
    update: `${BASE}/project`,
    delete: (code: string) => `${BASE}/project/${code}`,
    detailsByManager: (userName: string) => `${BASE}/project/details/${userName}`,
    complete: (projectCode: string) => `${BASE}/project/manager/complete/${projectCode}`,
  },
  task: {
    list: `${BASE}/task`,
    byId: (id: number) => `${BASE}/task/${id}`,
    create: `${BASE}/task`,
    update: `${BASE}/task`,
    delete: (id: number) => `${BASE}/task/${id}`,
    employeePending: `${BASE}/task/employee/pending-tasks`,
    employeeArchive: `${BASE}/task/employee/archive`,
    employeeUpdate: `${BASE}/task/employee/update`,
  },
} as const;
